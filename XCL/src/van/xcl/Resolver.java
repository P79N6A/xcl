package van.xcl;

import java.util.List;

public interface Resolver {
	public void resolve(List<String> commands, XCLContext context);
}
