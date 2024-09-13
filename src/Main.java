import ru.whywhy.tevm.system.TESystem;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        TESystem sys = new TESystem();

        sys.start();

        sys.print_regs();
    }
}