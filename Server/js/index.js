class Game {
	constructor(cv) {
		// this.modal = new Modal('mscreen', 'mwindow', 'mmsg', 'mbtn');
		this.canvas = cv;
		this.ctx = this.canvas.getContext("2d");
		this.spd = 1;
		this.pressedKeys = {};
		this.usedKeys = ["ArrowUp", "ArrowDown", "ArrowLeft", "ArrowRight"];
		this.tick = 16;
		this.g = 3;
		this.tid = 0;
		this.gameState = {};
		// this.md = document.getElementById("go-wrapper");
		// document.getElementById('restart').onclick = this.start.bind(this);
		this.cam = new Camera(this.canvas);
		this.size = new Point(1000, 1000);
		console.log("start");
		this.connection = new Connection('ws://127.0.0.1:8080/game');
		this.connection.onstate = ((s) => {this.gameState = s}).bind(this);

		window.addEventListener('keydown', e => {
			// console.log(e.key);
			if (this.usedKeys.includes(e.key)) {
				e.preventDefault();
				if (!this.pressedKeys[e.key]) {
					this.pressedKeys[e.key] = true;
					this.processKey(e.key);
				}
			}
		});
		window.addEventListener('keyup', e => {
			if (this.usedKeys.includes(e.key)) {
				e.preventDefault();
				this.pressedKeys[e.key] = false;
			}
		});
		window.addEventListener('resize', this.resize.bind(this));
		this.resize();

		clearInterval(this.tid);
		this.tid = setInterval((() => {this.doTick();this.render()}).bind(this), this.tick);

		
	}
	processKey(key) {
		// console.log(key);
		let a = 0;
		switch (key) {
			case "ArrowUp": a = Math.PI / 2; break;
			case "ArrowDown": a = Math.PI / 2 * 3; break;
			case "ArrowLeft": a = Math.PI; break;
			case "ArrowRight": a = 0; break;
			default: return;
		}
		this.setAngle(a);
	}

	doTick() {
		// this.connection.sendRequest(((resp) => {console.log(resp); this.gameState = resp}).bind(this), 'getGameState');
	}

	render(){
		this.ctx.clearRect(0, 0, this.w, this.h);
		this.ctx.fillStyle = "hsl(100, 50%, 80%)";
		this.ctx.fillRect(...this.cam.ps(0, 0, this.size.x, this.size.y));
		this.ctx.strokeRect(...this.cam.ps(0, 0, this.size.x, this.size.y));
		this.ctx.fiilStyle = 'rgb(255, 0, 0)';

		for (let k in this.gameState) {
			let p = this.gameState[k];
			this.ctx.beginPath();
			this.ctx.fillStyle = 'rgb(255,0,0)';
			this.ctx.ellipse(...this.cam.ps(p.pos.x, p.pos.y, 11, 11), 0, 0, Math.PI * 2);
			this.ctx.fill();
			// clearInterval(this.tid);
		}
	}
	
	resize(){
		this.canvas.width = window.innerWidth;
		this.canvas.height = window.innerHeight;
		this.w = this.canvas.width;
		this.h = this.canvas.height;
		this.cam.resize(this.w, this.h);
	}

	setAngle(a){
		this.connection.sendRequest(() => {}, 'setAngle', a);
	}
}

class Entity {
	constructor(pos, size) {
		this.pos = pos;
		this.size = size;
	}
}
class Player extends Entity {
	constructor(pos, size, imgsrc) {
		super(pos, size);
		this.img = new Image();
		this.img.src = imgsrc;
		this.vy = 0;
	}
}

class Point {
	constructor(x, y) {
		this.x = x;
		this.y = y;
	}
}

class Camera{
	constructor(cv){
		this.pos = new Point(0, 0);
		this.size = new Point(0, 0);
		this.md = false;
		this.prevMousePos = new Point();
		this.scale = 1.0;
		cv.addEventListener('mousedown', e => {
			this.md = true;
			this.prevMousePos = this.getMousePos(e);
		});
		cv.addEventListener('mouseup', e => {this.md = false;});
		cv.addEventListener('mousemove', e => {
			if(this.md){
				let mp = this.getMousePos(e);
				this.pos.x -= (mp.x - this.prevMousePos.x) / this.scale;
				this.pos.y -= (mp.y - this.prevMousePos.y) / this.scale;
				this.prevMousePos = mp;
			}
			this.prevMousePos = this.getMousePos(e);
			// console.log(JSON.stringify(this.pos));
		});

		cv.addEventListener('wheel', e => {
			let os = this.scale;
			this.scale -= (e.deltaY / 100);
			this.pos.x += (this.prevMousePos.x / os) - (this.prevMousePos.x / this.scale);
			this.pos.y -= (this.size.y - this.prevMousePos.y / os) - (this.size.y - this.prevMousePos.y / this.scale);
		});
	}

	resize(x, y){
		this.size.x = x;
		this.size.y = y;
	}

	ps(x, y, w, h){
		return [(x - this.pos.x) * this.scale, (this.size.y - y - this.pos.y - h) * this.scale, w * this.scale, h * this.scale]
	}
	
	getMousePos(e){
		return new Point(e.pageX, e.pageY);
	}

	
}

class ModalWindow{
	constructor(msg, btxt, onclose){
		this.scr = document.createElement('div');
		this.scr.className = 'mscreen';
		document.body.insertBefore(this.scr, document.body.firstChild);
		this.wnd = document.createElement('div');
		this.wnd.className = 'mwindow';
		this.scr.appendChild(this.wnd);
		this.txt = document.createElement('h2');
		this.txt.innerText = msg;
		this.btn = document.createElement('button');
		this.btn.className = 'mbtn';
		this.btn.innerText = btxt;
		this.btn.onclick = (() => {});
		this.wnd.appendChild(this.txt);
		this.wnd.appendChild(this.btn);
		this.onbtn = onclose;
	}

	set onbtn(f){this.btn.onclick = f;}
	get onbtn(){return this.btn.onclick;}
	set message(t){this.txt.innerText = t;}
	get message(){return this.txt.innerText;}
	set buttonText(t){this.btn.innerText = t;}
	get buttonText(){return this.btn.innerText;}

	destroy(){
		document.body.removeChild(this.scr);
	}
}

let cv = document.getElementById("canvas");
let g = new Game(cv);