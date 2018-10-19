package van.xcl.cmd;

import java.util.Map;

import van.util.eval.dec.DecEval;
import van.util.eval.dec.DecEvalParser;
import van.xcl.Command;
import van.xcl.XCLConsole;
import van.xcl.Parameters;
import van.xcl.XCLContext;
import van.xcl.XCLCmdParser.XCLNode;
import van.xcl.XCLVar;

public class Calc implements Command {

	@Override
	public String name() {
		return "calc";
	}
	
	@Override
	public String description() {
		return "numeric calculator";
	}

	@Override
	public Parameters parameters() {
		Parameters parameters = new Parameters();
		parameters.add("expression");
		return parameters;
	}

	@Override
	public XCLVar execute(XCLNode node, Map<String, XCLVar> args, XCLConsole console, XCLContext context) {
		XCLVar calcStr = args.get("expression");
		DecEvalParser p = new DecEvalParser(CalcBasic.class);
		DecEval eval = p.parse(calcStr.toString());
		String result = eval.eval(context).toPlainString();
		console.output(calcStr.toString() + " = " + result);
		return new XCLVar(result);
	}
}
