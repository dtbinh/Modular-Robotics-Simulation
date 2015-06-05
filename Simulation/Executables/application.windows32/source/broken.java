import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class broken extends PApplet {

int numBalls = 100;
int id = numBalls + 1;
float spring = .30f;    // Spring force constant for collisions between balls
float gravity = 0.0f; //Vertical Gravitation force
float k = -10.0f; // Repulsive Force between balls
float b = 0.1f; // Sliding friction coefficient
float Radius = 5;  //Radius of discbots
float friction = -.9f; // Friction Coefficient against boundaries
Ball[] balls = new Ball[numBalls];

float freq = PI/40;    //oscillation frequency
long t = 0;            //global time parameter 
float phaseoffset = 0; //phase individual discbots

boolean expanding = false;    // true: broken bots instantly expand to 
                              // max radius
boolean pause = false;        // to pause simulation
boolean record = false;       // to write data to output files

// Handles for output files
PrintWriter output;  
PrintWriter out1;
PrintWriter out2;

int time;

public void setup() {
  size(700, 700);
    output = createWriter("r.dat");
    out1 = createWriter("x.dat");
    out2 = createWriter("y.dat"); 
  for (int i = 0; i < numBalls; i++) {
    //Random Initializations:
    balls[i] = new Ball(random(width), random(height), 2*Radius, random(freq), i*PI/5,i,balls);
    if (i%1 != 0){ balls[i].broken = true;}
    id++;
  }
  // Organized Initialization:
  /*int county = 0;
  int nrow = 20;
  int ncol = numBalls/nrow;
  for (int i = 0; i < ncol; i++) {
    for(int j = 0; j < nrow; j++){
      
      println(county);
      //line (horizontal):
      balls[county] = new Ball(i*width/ncol, j*height/nrow, 2*radius, freq, (i)*PI/5, county, balls);
      id++;county++;
    }
  }*/
  
  noStroke();
  fill(255, 204);
}

public void draw() {
  if(!pause){  
    background(0);
    for (int i = 0; i < numBalls; i++) {
      if(!balls[i].broken){
        balls[i].sizer();   //determine size of bot (if not broken)
      }
      else{
        balls[i].diameter = 4*Radius;  // set broken bot diameter  
      }
      balls[i].interact();   //determine updates to positions
    }
    for (int i = 0; i < numBalls; i++) {
      balls[i].move();        //update positions
      balls[i].display();     //display visual updates
      if(record && balls[i].broken){
        // record positions of broken bots
         output.println(balls[i].x + "\t" + balls[i].y);  
      } 
    } 
  }
  
  if(record){
    //record positions for every bot
    for(int i = 0; i < numBalls; i++){
      out1.print(balls[i].x + "\t");
      out2.print(balls[i].y + "\t");
    }
    out1.print("\n");
    out2.print("\n");
  }
  
}

public void keyPressed(){
  println(key == ENTER );
  if(key == 32){
    pause = !pause; //play or pause
  }
  
  if(key == 112){
    saveFrame("ball-####.png");  //save screenshot
  }
  
  if(key == ENTER){
    record = !record;
    if(!record){
      // close file handles and exit script
        output.flush(); out1.flush(); out2.flush();
        output.close(); out2.close(); out2.close();
        println("TIME: " + (millis() - time)/1000 + "s");
        println("DONE");
        exit();        
      }  
    else{
      //begin recording
      println("RECORDING:");
      time = millis();
    }
  }
    
  if(key == CODED){
    if(keyCode == UP){
      //resize bots
      int i = 0; 
      int id = -1;
      while(i < numBalls && id < 0){
        if(balls[i].contains(mouseX,mouseY)){
          id = i;
        };
        i++;
      }
      if(id != -1){
        balls[id].phase += 0.1f;
        balls[id].sizer();
        
        /*if(balls[id].diameter > 2*radius){
          balls[id].diameter += 2;
          balls[id].phase = asin(3.0 - balls[id].diameter/radius);
          balls[id].t = 0;
          println(balls[id].diameter);
        }*/
      }

    }
    
    else if(keyCode == DOWN){
      //resize bots 
      int i = 0; 
      int id = -1;
      while(i < numBalls && id < 0){
        if(balls[i].contains(mouseX,mouseY)){
          id = i;
        };
        i++;
      }
      if(id != -1){
        balls[id].phase -= 0.1f;
        balls[id].sizer();
        
        
        
        /*if(balls[id].diameter > 2*radius){
          balls[id].diameter -= 2;
          balls[id].phase = asin(3.0 - balls[id].diameter/radius);
          balls[id].t = 0;
          println(balls[id].diameter);
        }*/
      }
    }
    step();
  } 
}

public void mouseClicked(){  
  //click to break/unbreak bots
  println("(" + mouseX + ", " + mouseY + ")");
  for(int i = 0; i < numBalls; i++){ 
    if(balls[i].contains(mouseX,mouseY)){
      balls[i].broken = !balls[i].broken;
      step();
    }
    if(expanding && balls[i].broken){
      balls[i].diameter = 4*balls[i].radius;
    }
  } 
}

public void step(){
  background(0);
  for (int i = 0; i < numBalls; i++) {  
    balls[i].interact(); 
  }
  for (int i = 0; i < numBalls; i++) {
    balls[i].move();
    balls[i].display(); 
  } 
}

/*
class for ball (provided by "Bouncy Bubbles" example sketch on 
Processing website) with additional updates to incorporate 
DiscBot behaviors.
*/
class Ball {
  
  private long t = 0;
  private float x, y;
  private float diameter;
  private float radius;
  private float w0;
  private float phase;
  private float vx = 0;
  private float vy = 0;
  private int id;
  private boolean broken = false;
  Ball[] others;
 
  Ball(float xin, float yin, float din, float win,  float phasein, int idin, Ball[] oin) {
    x = xin;
    y = yin;
    diameter = din;
    radius = din/2;
    phase = phasein;
    w0 = win;
    id = idin;
    others = oin;
  } 
  
  Ball(float xin, float yin, float vxin, float vyin, float din, float win,  float phasein, int idin, Ball[] oin){
    this(xin, yin, din, win, phasein, idin, oin);
    vx = vxin;
    vy = vyin;
  }
  
  public void sizer(){
    this.diameter = 2*radius*(1.5f-0.5f*sin(w0*t + phase));
    t++;
  }
  
  public void interact() {
    vx = (1-b)*vx;
    vy = (1-b)*vy;
    
    for (int i = 0; i < numBalls; i++) {
      
      if(i != id){
        float dx = others[i].x - x;
        float dy = others[i].y - y;
        float distance = sqrt(dx*dx + dy*dy);
        
        float a = k/distance/distance;
        float angle = atan2(dy, dx);
        
        float ax = a*cos(angle);
        float ay = a*sin(angle); 
        
        
        float minDist = others[i].diameter/2 + diameter/2;
        if (distance < minDist) { 
          float targetX = x + cos(angle) * minDist;
          float targetY = y + sin(angle) * minDist;
          ax = ax + (targetX - others[i].x) * spring;
          ay = ay + (targetY - others[i].y) * spring;
        }
          vx -= ax;
          vy -= ay;
          others[i].vx += ax;
          others[i].vy += ay;
      }   
    }
  }
  
  public void move() {
    vy += gravity;
    x += vx;
    y += vy;
    if (x + diameter/2 > width) {
      x = width - diameter/2;
      vx *= friction; 
    }
    else if (x - diameter/2 < 0) {
      x = diameter/2;
      vx *= friction;
    }
    if (y + diameter/2 > height) {
      y = height - diameter/2;
      vy *= friction; 
    } 
    else if (y - diameter/2 < 0) {
      y = diameter/2;
      vy *= friction;
    }
  }
  
  public void display() {
    
    if(this.broken){
      fill(255);
      //fill(255,0,0);
    }
    else{
      fill(0, 50, 50);
      //fill(200);
    }
    ellipse(x, y, diameter, diameter);
  }
  
  // Helper Function:
  public boolean contains(float xin, float yin){
    float dx, dy, r;
    dx = xin - this.x;
    dy = yin - this.y;
    r = sqrt(dx*dx + dy*dy);
    
    return (r <= this.diameter/2);
  }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "broken" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
