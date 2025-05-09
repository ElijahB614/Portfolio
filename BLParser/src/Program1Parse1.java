import components.map.Map;
import components.program.Program;
import components.program.Program1;
import components.queue.Queue;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;
import components.statement.Statement;
import components.utilities.Reporter;
import components.utilities.Tokenizer;

/**
 * Layered implementation of secondary method {@code parse} for {@code Program}.
 *
 * @author Elijah Baugher
 *
 */
public final class Program1Parse1 extends Program1 {

    /*
     * Private members --------------------------------------------------------
     */

    /**
     * Parses a single BL instruction from {@code tokens} returning the
     * instruction name as the value of the function and the body of the
     * instruction in {@code body}.
     *
     * @param tokens
     *            the input tokens
     * @param body
     *            the instruction body
     * @return the instruction name
     * @replaces body
     * @updates tokens
     * @requires <pre>
     * [<"INSTRUCTION"> is a prefix of tokens]  and
     *  [<Tokenizer.END_OF_INPUT> is a suffix of tokens]
     * </pre>
     * @ensures <pre>
     * if [an instruction string is a proper prefix of #tokens]  and
     *    [the beginning name of this instruction equals its ending name]  and
     *    [the name of this instruction does not equal the name of a primitive
     *     instruction in the BL language] then
     *  parseInstruction = [name of instruction at start of #tokens]  and
     *  body = [Statement corresponding to the block string that is the body of
     *          the instruction string at start of #tokens]  and
     *  #tokens = [instruction string at start of #tokens] * tokens
     * else
     *  [report an appropriate error message to the console and terminate client]
     * </pre>
     */
    private static String parseInstruction(Queue<String> tokens, Statement body) {
        assert tokens != null : "Violation of: tokens is not null";
        assert body != null : "Violation of: body is not null";
        assert tokens.length() > 0 && tokens.front().equals("INSTRUCTION")
                : "" + "Violation of: <\"INSTRUCTION\"> is proper prefix of tokens";
        tokens.dequeue();
        //checks for instruction name
        Reporter.assertElseFatalError(
                tokens != null && Tokenizer.isIdentifier(tokens.front()),
                "NO IDENTIFIER");
        //Holds name of instruction
        String identifier = tokens.dequeue();
        //Checks that next token will be IS from Instruction identifier IS
        Reporter.assertElseFatalError(tokens != null && tokens.front().equals("IS"),
                "NO IS, FROM INSTRUCTION IDENTIFIER IS");
        tokens.dequeue();
        //parse tokens into given body
        body.parseBlock(tokens);
        //Checks for ending statements and returns instruction name
        String end1 = tokens.dequeue();
        Reporter.assertElseFatalError(tokens != null, "QUEUE IS EMPTY");
        String end2 = tokens.dequeue();
        Reporter.assertElseFatalError(tokens != null && end1.equals("END"),
                "INCORRECT ENDING STATEMENT");
        Reporter.assertElseFatalError(end2.equals(identifier), "This one");
        return identifier;
    }

    /*
     * Constructors -----------------------------------------------------------
     */

    /**
     * No-argument constructor.
     */
    public Program1Parse1() {
        super();
    }

    /*
     * Public methods ---------------------------------------------------------
     */

    @Override
    public void parse(SimpleReader in) {
        assert in != null : "Violation of: in is not null";
        assert in.isOpen() : "Violation of: in.is_open";
        Queue<String> tokens = Tokenizer.tokens(in);
        this.parse(tokens);
    }

    @Override
    public void parse(Queue<String> tokens) {
        assert tokens != null : "Violation of: tokens is not null";
        assert tokens.length() > 0
                : "" + "Violation of: Tokenizer.END_OF_INPUT is a suffix of tokens";
        //Check to make sure it is a program
        Reporter.assertElseFatalError(tokens != null && tokens.front().equals("PROGRAM"),
                "NOT A PROGRAM");
        tokens.dequeue();
        //Check for name of program
        Reporter.assertElseFatalError(
                tokens != null && Tokenizer.isIdentifier(tokens.front()),
                "INSTURCTION DOES NOT HAVE NAME");
        String identifier = tokens.dequeue();
        //Constructs the name of this
        this.setName(identifier);
        //Check to make sure next token will be IS from Program identifier IS
        Reporter.assertElseFatalError(tokens != null && tokens.front().equals("IS"),
                "EXPECTED IS AFTER IDENTIFIER");
        tokens.dequeue();
        //New context to hold instruction and parsed body
        Map<String, Statement> tempMap = this.newContext();
        //While loops that will not end until all instructions are parsed in program
        while (tokens.front().equals("INSTRUCTION")) {
            Statement a = this.newBody();

            String instruction = parseInstruction(tokens, a);
            tempMap.add(instruction, a);
        }
        //Constructs context for program
        this.swapContext(tempMap);
        //Checks that next token starts the body of program
        Reporter.assertElseFatalError(tokens != null && tokens.front().equals("BEGIN"),
                "EXPECTED BEGIN");
        tokens.dequeue();
        Statement b = this.newBody();
        //Constructs body of program
        this.swapBody(b);
        b.parseBlock(tokens);
        this.swapBody(b);
        //Checks ending statements
        String end1 = tokens.dequeue();
        String end2 = tokens.dequeue();
        Reporter.assertElseFatalError(
                tokens != null && end1.equals("END") && end2.equals(identifier),
                "INCORRECT ENDING STATEMENT");

    }

    /*
     * Main test method -------------------------------------------------------
     */

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        SimpleReader in = new SimpleReader1L();
        SimpleWriter out = new SimpleWriter1L();
        /*
         * Get input file name
         */
        out.print("Enter valid BL program file name: ");
        String fileName = in.nextLine();
        /*
         * Parse input file
         */
        out.println("*** Parsing input file ***");
        Program p = new Program1Parse1();
        SimpleReader file = new SimpleReader1L(fileName);
        Queue<String> tokens = Tokenizer.tokens(file);
        file.close();
        p.parse(tokens);
        /*
         * Pretty print the program
         */
        out.println("*** Pretty print of parsed program ***");
        p.prettyPrint(out);

        in.close();
        out.close();
    }

}
