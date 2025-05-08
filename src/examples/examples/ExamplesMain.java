package examples;

public class ExamplesMain {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err
                    .println("Please pass an example name as an argument!\n" + "Possible example names:\n"
                            + "- Parser\n" + "- Player\n" + "- Recorder\n" + "- Writer");
            System.exit(1);
            return;
        }
        String name = args[0];
        name = Character.toUpperCase(name.charAt(0)) + name.substring(1).toLowerCase();
        try {
            Class<?> exClass = Class.forName("examples.VCD" + name + "Example");
            System.err.println("Starting VCD" + name + "Example...");
            System.err.println();
            exClass.getMethod("main", String[].class).invoke(null, (Object) new String[0]);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Unknown example name: " + name);
        }
    }
}
