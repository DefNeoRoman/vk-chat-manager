package analyzer;

public class SandBox {
    public static void main(String[] args) {
        String s = "bla, bla.";
        String nov = s.replaceAll("\\W"," ");
        System.out.println(nov);
    }
}
