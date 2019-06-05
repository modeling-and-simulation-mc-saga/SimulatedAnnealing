import java.awt.Point;
import java.util.List;
model.Simulation sys;
int tInterval=100;
final int tMax = 500;
final double coolingRate=0.98;
final float fx = 2.;
final float fy = 2.;
final float r =10.;
int t=0;

//initialize
void setup(){
  size(900,900);
  smooth();
  frameRate(10);
  String lines[]=loadStrings("../points.txt");
  sys = new Simulation(lines);
}

void draw(){
  if (t>=tMax){
    noLoop();
  }
  translate(50,850);
  background(#F7EBD4);
  for(int i=0;i<tInterval;i++){
    sys.oneMonteCarloStep();
  }
  List<Point> path = sys.currentPath();
  {
    stroke(0,128,0);
    strokeWeight(5);
    Point p = path.get(0);
    for(int i=0;i<path.size();i++){
      int j = (i+1)%path.size();
      Point q = path.get(j);
      line(fx*p.x,-fy*p.y,fx*q.x,-fy*q.y);
      p = q;
    }
  }
  fill(255,0,0);
  stroke(128,0,0);
  strokeWeight(1);
  for(Point p:path){
    ellipse(fx*p.x,-fy*p.y,2*r,2*r);
  }
  sys.cooling(coolingRate);
  t++;
  tInterval=(int)(tInterval/coolingRate);
}

void mousePressed(){
  exit();
}
