var title, explain;
var w = 100;

var backC;

var index = 0;
var imgs;
var upImage, downImage;

function setup() {
  createCanvas(windowWidth, windowHeight - 80);
  title = createElement('span', '<div><h1>８ミリフィルムプロジェクト</h1><p>インクジェットプリンタやレーザカッターで，8mmフィルム自体を自作（キネコ）しています．そのフィルムを用いた作品集です．</p></div><div><h3 id="imgTitle"></h3><p id="imgContent"></p></div>');
  explain = createElement('span', '');
  fixTextPosition();
  backC = color(239, 173, 7);
  w = height * 2 / 5;
  upImage = loadImage("./up.png");
  downImage = loadImage("down.png");
  loadImages();
  updateExplain();
  textSize(60);
  textAlign(CENTER, CENTER);
}

function loadImages() {
  imgs = Array();
  var i = 0;
  imgs[i++] = { img: loadImage("badapple/thumbnail.jpg"), title: "BadApple!! 影絵", content: "初めての長編自作フィルムです．白黒．作り方の詳細などは<a href='badapple/index.html'>こちら</a>",link:"http://www.nicovideo.jp/watch/sm28787820" };
  imgs[i++] = { img: loadImage("sha.png"), title: "舎利禮文", content: "初のカラーフィルムです．" ,link:"http://www.nicovideo.jp/watch/sm28845675"};
  imgs[i++] = { img: loadImage("ROD.png"), title: "R.O.D -The TV- OP", content: "カラーの鮮やかなフィルムです．",link:"http://www.nicovideo.jp/watch/sm28869077" };
  imgs[i++] = { img: loadImage("film.png"), title: "自作フィルムのつくりかた", content: "フィルム制作の解説動画です．",link:"http://www.nicovideo.jp/watch/sm28876844" };
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
  fill(255, sin((onPlayButton ? 0.12 : 0.06) * frameCount) * 80 + 80);
  rect(x, y, w, h);
  fill(255);
  text("play!", x + w / 2, y + h / 2);
  //console.log(movingTime);
}

function drawFilm() {
  noStroke();
  fill(0);
  rect(0, 0, w, height * 1.5);
  var h = w / (2 / 5) / 2 * 3 / 7;
  fill(255);
  if (movingTime == 10) {
    movingTime = 0;
    index = (index + moving + imgs.length) % imgs.length;
    updateExplain();
    moving = 0;
  }
  for (var i = 0; i < 7; i++) {
    fill(backC);
    rect(15, 10 + h * i + h / 2 - w / 20 - moving * h * movingTime / 10 - h, w / 15, w / 10);
    if(moving==0&&i==3){
      fill(255,sin(0.04 * frameCount) * 75+150,sin(0.04 * frameCount) * 75+150);
      rect(35 + w / 15, 5 + h * i - moving * h * movingTime / 10 - h, w - 40 - w / 15, h);
    }
    fill(255);
    rect(40 + w / 15, 10 + h * i - moving * h * movingTime / 10 - h, w - 50 - w / 15, h - 10);
    var tint1 = 255;
    //console.log(imgs);
    if (2 <= i && i <= 4) {
      //console.log(i+" "+((imgs.length+index+i-3)%imgs.length));
      image(imgs[(imgs.length + index + i - 3) % imgs.length].img, 40 + w / 15, 10 + h * i - moving * h * movingTime / 10 - h, w - 50 - w / 15, h - 10);
    }
    if (i == ((moving == -1) ? 0 : 1)) {
      image(upImage, 40 + w / 15, 10 + h * i - moving * h * movingTime / 10 - h, w - 50 - w / 15, h - 10);
    }
    if (i == ((moving == 1) ? 6 : 5)) {
      image(downImage, 40 + w / 15, 10 + h * i - moving * h * movingTime / 10 - h, w - 50 - w / 15, h - 10);
    }
  }
  if (movingTime != 0) {
    movingTime++;
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
  if (keyCode === ENTER) {
    play();
    return;
  }
  if (moving != 0 && movingTime == 0) {
    movingTime = 1;
  }
}
var scroll=0;
function mouseWheel(event) {
  if(event.delta<-2){
    moving=-1;
  }
  if(2<event.delta){
    moving=1;
  }
  if (moving != 0 && movingTime == 0) {
    movingTime = 1;
  }
}
function updateExplain() {
  document.getElementById("imgTitle").innerHTML = imgs[index].title;
  document.getElementById("imgContent").innerHTML = imgs[index].content;
}
function mouseMoved() {
  var w = 200;
  var h = w / 2.5;
  var x = width - w - 50;
  var y = height - h - 50;
  onPlayButton = (x <= mouseX && mouseX <= x + w && y <= mouseY && mouseY <= y + h);
}
function mouseReleased() {
  if (onPlayButton) {
    play();
  }
}
function play() {
  print("play");
  window.open(imgs[index].link, '_blank');
}

function windowResized() {
  resizeCanvas(windowWidth, windowHeight - 80);
  w = height * 2 / 5; //min(width/2,height/2);
  fixTextPosition();

}

function fixTextPosition() {
  title.position(width / 2, 80);

  //explain.position(width/2, 50);
}