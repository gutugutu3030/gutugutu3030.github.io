var title,explain;
var w = 100;

var backC;

var index = 0;
var imgs;
var upImage,downImage;

function setup() {
  createCanvas(windowWidth, windowHeight-80);
  title = createElement('span', '<h1>８ミリフィルムプロジェクト</h1><p>インクジェットプリンタやレーザカッターで，8mmフィルム自体を自作（キネコ）しています．そのフィルムを用いた作品集です．</p>');
  fixTextPosition();
  backC = color(239, 173, 7);
  w = height * 2 / 5;
  upImage=loadImage("./up.png");
  downImage=loadImage("down.png");
  loadImages();
  textSize(60);
  textAlign(CENTER,CENTER);
}

function loadImages(){
  imgs=Array();
  var i=0;
  var imgTitle=["BadApple!! in 8mm Film"];
  var imgTitle=["BadApple!! in 8mm Film"];
  imgs[i++]={img:loadImage("badapple.png"),title:"BadApple!! in 8mm Film",contant:"Bad Apple!! 影絵を8mm映写機で再生"};
}


function draw() {
  background(backC);
  push();
  rotate(-PI / 20);
  translate(10, -10);
  drawFilm();
  pop();
  drawButton();
}

var onPlayButton = false;

function drawButton() {
  var w = 200;
  var h = w / 2.5;
  var x = width - w - 50;
  var y = height - h - 50;
  stroke(0);
  fill(52, 202, 255);
  rect(x, y, w, h);
  fill(255,sin((onPlayButton?0.12:0.06)*frameCount)*80+80);
  rect(x, y, w, h);
  fill(255);
  text("play!",x+w/2,y+h/2);
}

function drawFilm() {
  noStroke();
  fill(0);
  rect(0, 0, w, height * 1.5);
  var h = w / (2 / 5) / 2 * 3 / 7;
  fill(255);
  if (movingTime == 10) {
    movingTime = 0;
    moving = 0;
  }
  for (var i = 0; i < 7; i++) {
    fill(backC);
    rect(15, 10 + h * i + h / 2 - w / 20 - moving * h * movingTime / 10 - h, w / 15, w / 10);
    fill(255);
    rect(40 + w / 15, 10 + h * i - moving * h * movingTime / 10 - h, w - 50 - w / 15, h - 10);
    if(i==1){
      image(upImage,40 + w / 15, 10 + h * i - moving * h * movingTime / 10 - h, w - 50 - w / 15, h - 10);
    }
  }
}

var moving = 0;
var movingTime = 0;

function keyPressed() {
  if (keyCode === UP_ARROW) {
    moving = -1;
  }
  if (keyCode === DOWN_ARROW) {
    moving = 1;
  }
  if(keyCode===ENTER){
    play();
    return;
  }
  if (moving != 0 && movingTime == 0) {
    movingTime = 1;
  }
}

function mouseMoved() {
  var w = 200;
  var h = w / 2.5;
  var x = width - w - 50;
  var y = height - h - 50;
  onPlayButton = (x <= mouseX && mouseX <= x + w && y <= mouseY && mouseY <= y + h);
}
function mouseReleased(){
  if(onPlayButton){
    play();
  }
}
function play(){
  print("play");
}

function windowResized() {
  resizeCanvas(windowWidth, windowHeight-80);
  w = height * 2 / 5; //min(width/2,height/2);
  fixTextPosition();

}

function fixTextPosition(){
  title.position(width/2, 80);
  //explain.position(width/2, 50);
}