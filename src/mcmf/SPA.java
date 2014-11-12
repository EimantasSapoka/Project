package mcmf;

public class SPA {
   public static void main (String [] args) {
      Data data = new Data(args[0],args[1]);
      data.readData();
      data.drawGraphAndFindMax();
   }
}
