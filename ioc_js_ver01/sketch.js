var history = {
  log: new Array(),
  index: -1
}

var circuit = {
  map: new Array(),
  x: 0,
  y: 0,
  w: 0,
  h: 0
};

var se = {
  connect: null,
  disconnect: null
}

var c;
var flg = 0;
var touch = null;
var inClick = false;

var menu = {
  img: new Array(),
  r: 0,
  margin: 0
};

var line;

function setup() {
  //createCanvas(windowWidth, windowHeight);
  createCanvas(window.innerWidth, window.innerHeight);

  c = [color(0, 0, 0), color(255, 0, 0), color(0, 0, 255)];
  circuit.map = new Array(5);
  for (var i = 0; i < circuit.map.length; i++) {
    circuit.map[i] = new Array(5);
    for (var j = 0; j < circuit.map[i].length; j++) {
      circuit.map[i][j] = 0;
    }
  }
  menu.img = new Array(4);
  var path = getURL();
  path = path.substring(0, path.length - 1);
  var aa = path.indexOf("index.");
  path = path.substring(0, aa);
  menu.img[0] = loadImage(path + "data/menu/undo.png");
  menu.img[1] = loadImage(path + "data/menu/redo.png");
  menu.img[2] = loadImage(path + "data/menu/save.png");
  menu.img[3] = loadImage(path + "data/menu/load.png");
  se.connect = loadSound(path + "data/se/connect.mp3");
  se.disconnect = loadSound(path + "data/se/disconnect.mp3");
  setSize();
  history.log = new Array(1);
  history.log[0] = new Array();
  history.index = -1;
  addHistory();
}

function windowResized() {
  resizeCanvas(windowWidth, windowHeight);
  setSize();
}

function draw() {
  background(255);
  drawMenu();
  if (flg != 0) {
    var preTouchPoint = line[line.length - 1];
    var nowTouchPoint = {
      x: getXInt(),
      y: getYInt()
    };
    if (preTouchPoint.x != nowTouchPoint.x || preTouchPoint.y != nowTouchPoint.y) {
      line.push(nowTouchPoint);
    }
  }
  drawCircuit();
}

function setSize() {
  setMenuSize();
  var val = min(width, height - menu.r);
  setCircuitSize((width - val) / 2, menu.r, val, val);
}

function setCircuitSize(x, y, w, h) {
  circuit.x = x;
  circuit.y = y;
  circuit.w = w;
  circuit.h = h;
}

function drawCircuit() {
  noStroke();
  fill(0);
  rect(circuit.x, circuit.y, circuit.w, circuit.h);
  stroke(255);
  strokeWeight(1);
  var rw = circuit.w / circuit.map.length,
    rh = circuit.h / circuit.map[0].length;

  for (var i = 0; i < circuit.map.length; i++) {
    for (var j = 0; j < circuit.map[0].length; j++) {
      fill(c[circuit.map[i][j]]);
      rect(circuit.x + rw * i, circuit.y + rh * j, rw, rh);
    }
  }
  if (flg === 0) {
    return;
  }
  fill(c[flg]);
  for (var i = 0; i < line.length; i++) {
    rect(circuit.x + rw * line[i].x, circuit.y + rh * line[i].y, rw, rh);
  }

}

function ifOut() {
  return ((mouseIsPressed) ? mouseX : touchX) < circuit.x || circuit.x + circuit.w < ((mouseIsPressed) ? mouseX : touchX) || ((mouseIsPressed) ? mouseY : touchY) < circuit.y || circuit.y + circuit.h < ((mouseIsPressed) ? mouseY : touchY);
}

function getIndex() {
  var hist = new Array(getMax() + 1);
  for (var i = 0; i < hist.length; i++) {
    hist[i] = 0;
  }
  for (var i = 0; i < circuit.map.length; i++) {
    for (var j = 0; j < circuit.map[0].length; j++) {
      hist[circuit.map[i][j]]++;
    }
  }
  for (var i = 1; i < hist.length; i++) {
    if (hist[i] == 0) {
      return parseInt(i);
    }
  }
  return hist.length;
}


function getMax() {
  var tmp = 0;
  for (var i = 0; i < circuit.map.length; i++) {
    var t = Math.max.apply(null, circuit.map[i]);
    if (t > tmp) tmp = t;
  }
  return tmp;
}

function postMap() {
  $.post(
    'http://ioc.local',
    map2string(),
    function(data, textStatus) {
      alart("test");
    }, 'html'
  );
}

function map2string() {
  var str = "";
  for (var i = 0; i < circuit.map.length; i++) {
    for (var j = 0; j < circuit.map[i].length; j++) {
      if (i == 0 && j == 0) {
        str = circuit.map[0][0];
        continue;
      }
      str = str + "," + circuit.map[i][j];
    }
  }
  return str;
}

function mousePressed() {
  if (((mouseIsPressed) ? mouseY : touchY) < menu.r) {
    switch (parseInt(min(max(0, ((mouseIsPressed) ? mouseX : touchX)), width - 1) * menu.img.length / width)) {
      case 0:
        m_undo();
        break;
      case 1:
        m_redo();
        break;
      case 2:
        m_save();
        break;
      case 3:
        m_load();
        break;
    }
    return;
  }
  if (ifOut()) return;
  inClick = true;
  touch = {
    x: getXInt(),
    y: getYInt()
  };
  line = new Array();
  line.push(touch);
  var onGrid = circuit.map[getXInt()][getYInt()];
  var dragIndex1 = ((onGrid === 0) ? getIndex() : onGrid);
  if (dragIndex1 > 2) return;
  flg = dragIndex1;
}

function mouseReleased() {
  if (!inClick) return;
  inClick = false;

  if (line.length == 1) {
    for (var i = 0; i < circuit.map.length; i++) {
      for (var j = 0; j < circuit.map[i].length; j++) {
        if (circuit.map[i][j] == flg) {
          circuit.map[i][j] = 0;
        }
      }
    }
    se.disconnect.play();
  } else {
    for (var i = 0; i < line.length; i++) {
      circuit.map[line[i].x][line[i].y]=flg;
    }
    se.connect.play();
  }
  flg = 0;
  postMap();
  addHistory();
}

function touchStarted() {
  mousePressed();
}

function touchEnded() {
  mouseReleased();
}

function getXInt() {
  return parseInt((getX() - circuit.x) / circuit.w * circuit.map.length);
}

function getYInt() {
  return parseInt((getY() - circuit.y) / circuit.h * circuit.map[0].length);
}


function getX() {
  return min(max(((mouseIsPressed) ? mouseX : touchX), circuit.x), circuit.x + circuit.w - 1);
}

function getY() {
  return min(max(((mouseIsPressed) ? mouseY : touchY), circuit.y), circuit.y + circuit.h - 1);
}

function setMenuSize() {
  menu.r = min(width / 4, 100);
  menu.margin = (width - 4 * menu.r) / 5;
}

function drawMenu() {
  for (var i = 0; i < menu.img.length; i++) {
    image(menu.img[i], menu.margin + (menu.r + menu.margin) * i, 0, menu.r, menu.r);
  }
}

function deepCopy(src) {
  var dst = new Array(src.length);
  for (var i = 0; i < dst.length; i++) {
    dst[i] = new Array(src[i].length);
    for (var j = 0; j < dst[i].length; j++) {
      dst[i][j] = parseInt(src[i][j]);
    }
  }
  return dst;
}

function addHistory() {
  // ((Array)(history.log)).slice(history.index + 1, history.log.length);
  history.log.slice(history.index + 1, history.log.length);
  history.index++;
  history.log[history.index] = deepCopy(circuit.map);
  //println(history.index+":"+history.log[history.index]);
}

function m_undo() {
  if (history.index <= 0) {
    return;
  }
  history.index--;
  circuit.map = deepCopy(history.log[history.index]);
}

function m_redo() {
  if (history.index >= history.log.length - 1) {
    return;
  }
  history.index++;
  circuit.map = deepCopy(history.log[history.index]);
}

function m_save() {
  saveStrings(history.log, "file");
}

function m_load() {
  loadStrings();
}