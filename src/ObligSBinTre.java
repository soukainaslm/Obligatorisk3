

////////////////// ObligSBinTre /////////////////////////////////

import java.util.*;

public class ObligSBinTre<T> implements Beholder<T>
{


  public static void main(String[]args){

  }

  private static final class Node<T>
  {
    private T verdi;                   // nodens verdi
    private Node<T> venstre, høyre;    // venstre og høyre barn
    private Node<T> forelder;          // forelder

    // konstruktør
    private Node(T verdi, Node<T> v, Node<T> h, Node<T> forelder)
    {
      this.verdi = verdi;
      venstre = v;
      høyre = h;
      this.forelder = forelder;
    }

    private Node(T verdi, Node<T> forelder)  // konstruktør
    {
      this(verdi, null, null, forelder);
    }

    @Override
    public String toString(){ return "" + verdi;}

  } // class Node

  private Node<T> rot;                            // peker til rotnoden
  private int antall;                             // antall noder
  private int endringer;                          // antall endringer

  private final Comparator<? super T> comp;       // komparator

  public ObligSBinTre(Comparator<? super T> c)    // konstruktør
  {
    rot = null;
    antall = 0;
    comp = c;
  }

  @Override
  public boolean leggInn(T verdi)
  {
    Objects.requireNonNull(verdi, "Ulovlig med nullverdier!");

    if(tom()){
      rot = new Node<T>(verdi,null);
    }

    else {
      Node<T> p = rot, q = null;               // p starter i roten
      int cmp = 0;                             // hjelpevariabel

      while (p != null)                        // fortsetter til p er ute av treet
      {
        q = p;                                 // q er forelder til p
        cmp = comp.compare(verdi, p.verdi);     // bruker komparatoren
        p = cmp < 0 ? p.venstre : p.høyre;     // flytter p
      }

      // p er nå null, dvs. ute av treet, q er den siste vi passerte

      p = new Node<>(verdi,q);                  // oppretter en ny node

      if (q == null) rot = p;                  // p blir rotnode
      else if (cmp < 0) q.venstre = p;         // venstre barn til q
      else q.høyre = p;                        // høyre barn til q


    }

    antall++;
    endringer++;
    return true;
  }

  @Override
  public boolean inneholder(T verdi)
  {
    if (verdi == null) return false;

    Node<T> p = rot;

    while (p != null)
    {
      int cmp = comp.compare(verdi, p.verdi);
      if (cmp < 0) p = p.venstre;
      else if (cmp > 0) p = p.høyre;
      else return true;
    }

    return false;
  }

  @Override
  public boolean fjern(T verdi)
  {
    if (verdi == null) return false;  // treet har ingen nullverdier

    Node<T> p = rot, q = null;   // q skal være forelder til p

    while (p != null){            // leter etter verdi

      int cmp = comp.compare(verdi,p.verdi);      // sammenligner
      if (cmp < 0) { q = p; p = p.venstre; }      // går til venstre
      else if (cmp > 0) { q = p; p = p.høyre; }   // går til høyre
      else break;    // den søkte verdien ligger i p
    }
    if (p == null) {
      return false;   // finner ikke verdi
    }

    if (p.venstre == null || p.høyre == null){  // Tilfelle 1) og 2)

      Node<T> b = p.venstre != null ? p.venstre : p.høyre;
      if (p == rot) {
        rot = b;
      }
      else if (p == q.venstre) {
        q.venstre = b;
        if(b!= null) {
          b.forelder = q;
        }
      }
      else {
        q.høyre = b;
        if(b!= null) {
          b.forelder = q;
        }
      }
    }
    else { // Tilfelle 3)

      Node<T> s = p, r = p.høyre;   // finner neste i inorden
      while (r.venstre != null)
      {
        s = r;    // s er forelder til r
        r = r.venstre;
      }

      p.verdi = r.verdi;   // kopierer verdien i r til p

      if (s != p) {
        s.venstre = r.høyre;
        if(r.høyre != null){
          r.forelder.høyre = s;
        }
      }
      else {
        s.høyre = r.høyre;
        if(r.høyre != null){
          r.forelder.høyre = s;

        }
      }
    }

    antall--;   // det er nå én node mindre i treet
    endringer++;
    return true;



  }

  public int fjernAlle(T verdi)
  {
    int verdiAntall = 0;
    while (fjern(verdi)) verdiAntall++;
    return verdiAntall;



  }

  @Override
  public int antall()
  {
    return antall;
  }

  public int antall(T verdi)
  {
    if(verdi.equals(null))return 0;


    int antallLike = 0;
    Node<T> p = rot;

    while( p!= null){
      int cmp = comp.compare(verdi, p.verdi);
      if(cmp<0)p=p.venstre;
      else{
        if(cmp==0) antallLike++;
        p = p.høyre;
      }

    }
    return antallLike;


    // throw new UnsupportedOperationException("Ikke kodet ennå!");
  }

  @Override
  public boolean tom()
  {
    return antall == 0;
  }

  @Override
  public void nullstill()
  {
    if(!(tom())){

      int antall = antall();
      if(antall == 1){
        fjern(rot.verdi);
        endringer++;
      } else {
        Node<T> p = rot;
        while (p.venstre != null) {
          p = p.venstre;
        }
        T verdi = p.verdi;

        for (int i = 0; i < antall; i++) {
          verdi = p.verdi;
          p = nesteInorden(p);
          fjern(verdi);
          endringer++;
        }
      }
    }
  }

  private static <T> Node<T> nesteInorden(Node<T> p)
  {
    if(p.høyre !=null){
      p = p.høyre;
      while(p.venstre != null){
        p = p.venstre;
      }
      return p;
    }else{
      while(p!=null){
        if(p.forelder!=null && p.forelder.høyre != p){
          return p.forelder;
        }
        p = p.forelder;
      }
    }
    return p;

  }

  @Override
  public String toString()
  {
    if(tom()) return "[]";


    StringBuilder sb = new StringBuilder();
    sb.append("[");

    Node<T> p = rot;
    while(p.venstre != null){
      p = p.venstre;
    }

    for(int i = 0; i < antall; i++){
      sb.append(p.verdi);
      if(i != (antall-1)) sb.append(", ");
      p = nesteInorden(p);
    }

    sb.append("]");


    return sb.toString();


  }

  public String omvendtString()
  {
    if(tom()) return "[]";
    StringBuilder sb = new StringBuilder();
    Deque<Node> stack = new ArrayDeque<>();
    sb.append("[");

    Node<T> p = rot;

    while(p.venstre != null){
      p = p.venstre;
    }

    for(int i = 0; i < antall; i++){
      stack.addFirst(p);
      p = nesteInorden(p);
    }


    for(int i = 0; i< antall; i++){
      sb.append(stack.pop());
      if(i!=(antall-1)) sb.append(", ");
      //stack.removeFirst();
    }

    sb.append("]");

    return sb.toString();



    //throw new UnsupportedOperationException("Ikke kodet ennå!");
  }

  public String høyreGren()
  {
    if(tom()) return "[]";


    StringBuilder sb = new StringBuilder();
    sb.append("[");

    Node<T> p = rot;
    sb.append(rot.verdi);

    while(p.høyre != null || p.venstre != null){
      while(p.høyre != null){
        p = p.høyre;
        sb.append(", " + p.verdi);
      }
      while(p.høyre == null && p.venstre != null){
        p = p.venstre;
        sb.append(", " + p.verdi);
      }
    }


    sb.append("]");

    return sb.toString();
    // throw new UnsupportedOperationException("Ikke kodet ennå!");
  }

  public String lengstGren()
  {

    if(tom()) return "[]";


    //Lager en hjelpe-stack, til å bevege seg nedover treeet, og finne lengst vei
    ArrayDeque<Node<T>> stack = new ArrayDeque<>();
    //Starter med rot-noden
    stack.addFirst(rot);
    Node<T> p = rot;

    //Mens stakken IKKE er tom
    //Stacker opp verdier og fjerner alltid første(siste)verdi
    while(!stack.isEmpty()){
      p = stack.removeLast();

      //Setter inn p i passende posisjon, om plassen er ledig eller ikke.
      if(p.høyre != null){
        stack.addFirst(p.høyre);
      }
      if(p.venstre != null){
        stack.addFirst(p.venstre);
      }

    }

    //hjelpeverdi- siste bladnode
    T verdi = p.verdi;
    // p = rot;
    StringBuilder sb = new StringBuilder();
    ArrayDeque<Node<T>> stakk2 = new ArrayDeque<>();
    stakk2.addFirst(p);
    sb.append("[");
    while(p.forelder != null){
      p = p.forelder;
      stakk2.addFirst(p);
    }

    sb.append(stakk2.pop());
    while (!stakk2.isEmpty()){
      sb.append(", ").append(stakk2.pop());
    }
    sb.append("]");
    return sb.toString();


  }

  public String[] grener() {

    if(tom()) return new String[]{};

    // Hjelpevariabler / stack
    ArrayDeque<Node<T>> stakk = new ArrayDeque<>();
    Node<T> p = rot;

    //Drar til nederste node - til venstre
    while (p.venstre != null){
      p = p.venstre;
    }

    //Sjekker om noden er en bladnode
    if(p.høyre == null && p.venstre == null){
      stakk.addLast(p);
    }

    //Itererer gjennom alle noder, og legger til bladnode(r) til hver gren,
    // og sjekker om de har nådd høyre(siste bladnode)
    //legger inn i stacken
    while (nesteInorden(p) != null){
      p = nesteInorden(p);
      if(p.venstre == null && p.høyre == null){
        stakk.addLast(p);
      }
    }

    //Hjelpevariabler
    int i = 0;
    String[] st = new String[stakk.size()];

    // Ferdig stringbuilder
    while(!stakk.isEmpty()){
      //
      Node<T> node = stakk.pop();
      StringBuilder sb = new StringBuilder();
      ArrayDeque<Node<T>> stakk2 = new ArrayDeque<>();
      stakk2.addFirst(node);
      sb.append("[");
      while(node.forelder != null){
        node = node.forelder;
        stakk2.addFirst(node);
      }

      sb.append(stakk2.pop());
      while (!stakk2.isEmpty()){
        sb.append(", ").append(stakk2.pop());
      }
      sb.append("]");

      st[i] = sb.toString();
      i++;
    }

    return st;
  }

  public String bladnodeverdier()
  {
    if(tom()) return "[]";

    // Hjelpevariabler / stack
    ArrayDeque<Node<T>> stakk = new ArrayDeque<>();
    Node<T> p = rot;

    //Drar til nederste node - til venstre
    while (p.venstre != null){
      p = p.venstre;
    }

    //Sjekker om noden er en bladnode
    if(p.høyre == null && p.venstre == null){
      stakk.addLast(p);
    }

    //Itererer gjennom alle noder, og legger til bladnode(r) til hver gren,
    // og sjekker om de har nådd høyre(siste bladnode)
    //legger inn i stacken
    while (nesteInorden(p) != null){
      p = nesteInorden(p);
      if(p.venstre == null && p.høyre == null){
        stakk.addLast(p);
      }
    }

    //Legger til stack verdiene i en string
    String bladNode = "[";
    bladNode += stakk.pop();
    while (!(stakk.isEmpty())){
      bladNode += ", " + stakk.pop();
    }
    bladNode += "]";

    return bladNode;
  }

  public String postString() {

    StringBuilder sb = new StringBuilder();
    sb.append("[");

    if(!tom()){
      Node<T> p = rot;
      finnNodeIterativt(p, sb);
    }

    sb.append("]");

    return sb.toString();
  }

  private void finnNodeIterativt(Node<T> p, StringBuilder sb){
    if(p.venstre != null) finnNodeIterativt(p.venstre, sb);
    if(p.høyre != null) finnNodeIterativt(p.høyre, sb);
    if(p.venstre == null && p.høyre == null);

    sb.append(p.verdi);
    if(p != rot) sb.append(", ");
  }

  @Override
  public Iterator<T> iterator()
  {
    return new BladnodeIterator();
  }

  private class BladnodeIterator implements Iterator<T>
  {
    private Node<T> p = rot, q = null;
    private boolean removeOK = false;
    private int iteratorendringer = endringer;

    private BladnodeIterator() { // konstruktør
      if (tom()) return;

      while (true){
        if (p.venstre != null) p = p.venstre;
        else if (p.høyre != null) p = p.høyre;
        else break;
      }
    }

    @Override
    public boolean hasNext()
    {
      return p != null;  // Denne skal ikke endres!
    }

    @Override
    public T next() {
      if (!hasNext())
        throw new NoSuchElementException("Ingen fler bladnoder!");
      else if (endringer != iteratorendringer)
        throw new ConcurrentModificationException("Endringer(" + endringer + ") != iteratorendringer(" + iteratorendringer +")");

      removeOK = true;

      q = p;
      T temp = p.verdi;
      while(hasNext()) {
        p = nesteInorden(p);
        if (p == null) return temp;

        if (p.venstre == null && p.høyre == null) return temp;
      }
      return temp;
    }

    @Override
    public void remove() {
      if (!removeOK)
        throw new IllegalStateException("Ikke lov å kalle på metoden!");

      removeOK = false;

      if (q.forelder == null)
        rot = null;
      else
      if (q.forelder.venstre == q) q.forelder.venstre = null;
      else q.forelder.høyre = null;

      antall--;
      endringer++;
      iteratorendringer++;
    }

  } // BladnodeIterator

} // ObligSBinTre
