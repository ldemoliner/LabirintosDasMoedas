package ia;

import java.util.ArrayList;
import java.util.Arrays;

public class Agente implements MapObject{
    private int x, y, pontos = 0;

    private Porta portaLocal;
    private ArrayList<Bau> bausLocal = new ArrayList<>();

    private ArrayList<Saco> inventario = new ArrayList<>();

    private Object[][] vision;

    public Agente(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getPontos() {
        return pontos;
    }

    public void setPontos(int pontos) {
        this.pontos = pontos;
    }

    public ArrayList<Saco> getInventario() {
        return inventario;
    }

    public void setInventario(ArrayList<Saco> inventario) {
        this.inventario = inventario;
    }

    @Override
    public String toString() {
        return "@";
    }

    public int getMoedas() {
        int acc = 0;
        for(int i = 0; i < inventario.size(); i++){
            acc += inventario.get(i).getQuantidade();
        }
        return acc;
    }

    public void setVisible(){
        Object[][] mapa = Main.mapa;

        int xSize = Math.min(x+2, 9) - Math.max(x-2, 0) + 1;
        int ySize = Math.min(y+2, 9) - Math.max(y-2, 0) + 1;
        Object[][] res = new Object[xSize][ySize];

        System.out.println(res.length);
        System.out.println(res[1].length);

        for(int i = Math.max(x-2, 0); i <= Math.min(x+2, 9); i++){
            //System.out.println("i: "+i);
            for(int j = Math.max(y-2, 0); j <= Math.min(y+2, 9); j++){
            //System.out.println("j: "+j);
                res[i - Math.max(x-2, 0)][j - Math.max(y-2, 0)] = mapa[i][j];
            }
        }
        vision = res;
    }

    public void start(){
        System.out.println("Start");
        setVisible();
        printVision();

        findDoor();
        findChests();

        Saco sack = findSack();
        if(sack != null){
            moveTo(sack);
        }
    }

    public void moveTo(Object o){
        System.out.println("Moveto");
        int x, y;
        x = ((MapObject)o).getX();
        y = ((MapObject)o).getY();

        Object[] path = aStar(this.x, this.y, x, y);
    }

    public Object[] aStar(int x1, int y1, int x2, int y2){
        System.out.println("aStar");
        Object[][] mapa = Main.mapa;
        ArrayList<Object> visited = new ArrayList<>();
        ArrayList<Object> toVisit = new ArrayList<>();
        ArrayList<Object> cameFrom = new ArrayList<>();
        ArrayList<Integer> weights = new ArrayList<>();

        //for(Object o : getNeighbors(mapa, x,y)){
        //    insertIntoArray(toVisit, weights, o, 1);
        //    cameFrom.add(null);
        //}

        MapObject curr = (MapObject) mapa[x1][y1];
        int weight = 0;
        Object from = null;

        visited.add(curr);

        do{
            System.out.println("aaaaaaaaaaaaa");
            ArrayList<Object> neighbors = getNeighbors(mapa, curr.getX(),curr.getY());
            for(Object o : neighbors){
                Arrays.toString(neighbors.toArray());
                if(!visited.contains(o)) {
                    int x = ((MapObject) o).getX();
                    int y = ((MapObject) o).getY();
                    insertIntoArray(toVisit, weights, o, weight + 1 + distance(x, y, x2, y2));
                    cameFrom.add(curr);
                }
            }
            //System.out.println(Arrays.toString(toVisit.toArray()));
            //System.out.println(Arrays.toString(weights.toArray()));
            //System.out.println(Arrays.toString(visited.toArray()));

            curr = (MapObject) toVisit.remove(0);
            weight = weights.remove(0);
            visited.add(curr);
        }while((((MapObject)curr).getX() != x2 && ((MapObject)curr).getY() != y2) || toVisit.size() != 0);

        System.out.println("obj: "+curr);
        //if((((MapObject)curr).getX() == x2 && ((MapObject)curr).getY() == y2))
            //return curr;
        return null;
    }

    public void insertIntoArray(ArrayList<Object> objs, ArrayList<Integer> weights, Object o, Integer weight){
        System.out.println("Insert into Array");
        int pos = 0;

        while(pos < objs.size()){
            if(weights.get(pos) > weight){
                objs.add(pos, o);
                weights.add(pos, weight);
                return;
            }
            pos++;
        }
        objs.add(o);
        weights.add(weight);
    }

    public ArrayList<Object> getNeighbors(Object[][] mapa, int x1, int y1){
        System.out.println("get neighbors");
        ArrayList<Object> res = new ArrayList<>();

        for(int i = 0; i < mapa.length; i++) {
            for (int j = 0; j < mapa[i].length; j++) {
                x = ((MapObject)mapa[i][j]).getX();
                y = ((MapObject)mapa[i][j]).getY();
                if(distance(x1, y1, x, y) <= 2 &&
                   distance(x1, y1, x, y) > 0 &&
                   mapa[i][j] instanceof Muro == false){
                    res.add(mapa[i][j]);
                }
            }
        }

        return res;
    }

    public int distance(int x1, int y1, int x2, int y2){
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    public void printVision(){
        for(int i = 0; i < vision.length; i++){
            for(int j = 0; j < vision[i].length; j++){
                System.out.print(vision[i][j]);
            }
            System.out.println();
        }
    }

    public Porta findDoor(){
        for(int i = 0; i < vision.length; i++){
            for(int j = 0; j < vision[i].length; j++){
                if(vision[i][j] instanceof Porta){
                    portaLocal = (Porta)vision[i][j];
                    return portaLocal;
                }
            }
        }

        return null;
    }

    public void findChests(){
        for(int i = 0; i < vision.length; i++){
            for(int j = 0; j < vision[i].length; j++){
                if(vision[i][j] instanceof Bau){
                    for(int k = 0; k < bausLocal.size(); k++)
                        if(!bausLocal.contains(vision[i][j]))
                            bausLocal.add((Bau)vision[i][j]);
                }
            }
        }
    }

    public Saco findSack(){
        for(int i = 0; i < vision.length; i++){
            for(int j = 0; j < vision[i].length; j++){
                if(vision[i][j] instanceof Saco){
                    return (Saco)vision[i][j];
                }
            }
        }

        return null;
    }
}
