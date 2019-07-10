(function () {
    class Connection{
        constructor(addr){
            this.nextid = 0;
            this.promiseControl = [];
            this.ws = new WebSocket(addr);
            this.id = -1;
            this.onstate = (s) => {};
            this.ws.onmessage = ((msg) => {
                if(this.id === -1){
                    this.id = parseInt(msg.data);
                    return;
                }
                let resp = JSON.parse(msg.data);
                if(resp.hasOwnProperty('name')){
                    this.onstate(resp.response)
                }
            }).bind(this);
        }
    
        sendRequest(name, ...args){
            this.ws.send(JSON.stringify({op: name, args: args, rid: this.nextid++}))
        }
    }

    var Camera = {
        pos: {
            x: 0,
            y: 0
        },
    }

    var ws = new Connection(`ws://${document.domain}:8080/game`)
    ws.onstate = (e) => {
        entities = e.map(ent => {
            let newEnt = Object.assign({}, ent)
            newEnt.size = {
                x: newEnt.sizex,
                y: newEnt.sizey
            }
            return newEnt
        })
    }

    var canv = document.getElementById("canv")
    var ctx = canv.getContext("2d")
    var entities = []
    var sprites = {
        ship: new Image(),
        buttonL: new Image(),
        buttonR: new Image()
    }
    sprites.ship.src = "img/ship.png"
    sprites.buttonL.src = "img/buttonL.png"
    sprites.buttonR.src = "img/buttonR.png"

    var lastMousePosition = {
        x: 0, y: 0
    }
    var mapSize = {
        x: 2000, 
        y: 2000
    }
    var highlight = {
        left: false,
        right: false
    }

    var fields = [
        new Image(),    
    ]
    fields[0].src = "img/sea.png"
    var offset = 5

    function distance(p1, p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2))
    }

    function init() {
        document.addEventListener("keydown", (e) => {
            let you = entities.filter(ent => ent.id == ws.id)[0]
            switch (e.keyCode) {
                case 49:
                    shot1()
                    highlight.left = true
                    break
                
                case 50:
                    shot2()
                    highlight.right = true
                    break
                
                case 68:
                    ws.sendRequest("changeAngle", you.angle + 0.1)
                    break
                
                case 65:
                    ws.sendRequest("changeAngle", you.angle - 0.1)
                    break
            }
        })

        canv.addEventListener("mousemove", (e) => {
            lastMousePosition.x = e.clientX - canv.getBoundingClientRect().left
            lastMousePosition.y = e.clientY - canv.getBoundingClientRect().top
        })

        canv.addEventListener("mousedown", (e) => {
            let center = 0.5 * canv.width
            let leftButtonCoords = {
                x: center - 0.05 * canv.width,
                y: 0.8 * canv.height
            }

            if (lastMousePosition.x >= leftButtonCoords.x && lastMousePosition.x <= leftButtonCoords.x + 60 &&
                lastMousePosition.y >= leftButtonCoords.y && lastMousePosition.y <= leftButtonCoords.y + 60) {
                shot1()
                highlight.left = true
            }

            if (lastMousePosition.x >= leftButtonCoords.x + 0.1 * canv.width && lastMousePosition.x <= leftButtonCoords.x + 60 + 0.1 * canv.width &&
                lastMousePosition.y >= leftButtonCoords.y && lastMousePosition.y <= leftButtonCoords.y + 60) {
                shot2()
                highlight.right = true
            }
        })
    }

    function max(a, b) {
        return a > b ? a : b
    }

    function drawRotatedImage(image, x, y, sizeX, sizeY, angle) {
        ctx.save()
        ctx.translate(x, y)
        ctx.rotate(angle)
        ctx.drawImage(image, -(sizeX / 2), -(sizeY / 2), sizeX, sizeY)
        //ctx.strokeRect(-(sizeX / 2), -(sizeY / 2), sizeX, sizeY)
        ctx.restore()
    }

    function renderField() {
        for (let i = -fields[0].width; i < canv.width + fields[0].width; i += fields[0].width) {
            for (let j = -fields[0].height; j < canv.height + fields[0].height; j += fields[0].height) {
                ctx.save()
                ctx.translate(offset - Camera.pos.x % fields[0].width, -Camera.pos.y % fields[0].height)
                ctx.drawImage(fields[0], i, j)
                ctx.restore()
            }
        }
    }

    function renderEntities() {
        //ctx.transform(1, 0, 0, -1, 0, canv.height)
        let you = entities.filter(ent => ent.id == ws.id)[0]
        Camera.pos = Object.assign({}, you.pos)

        for (let ent of entities) {
            if (ent.pos.x >= (Camera.pos.x - window.innerWidth / 2 - max(ent.size.x, ent.size.y)) && ent.pos.x <= (Camera.pos.x + window.innerWidth / 2 + max(ent.size.x, ent.size.y))
                && ent.pos.y >= (Camera.pos.y - window.innerHeight / 2 - max(ent.size.x, ent.size.y)) && ent.pos.y <= (Camera.pos.y + window.innerHeight / 2 + max(ent.size.x, ent.size.y))) {
                drawRotatedImage(sprites.ship,
                    ent.pos.x - Camera.pos.x + window.innerWidth / 2,
                    ent.pos.y - Camera.pos.y + window.innerHeight / 2, ent.size.x, ent.size.y, ent.angle)
                
                ctx.strokeStyle = "black"
                ctx.lineWidth = 2
                ctx.fillStyle = "red"

                hpbaroffset = {
                    x: -50, 
                    y: -120
                }

                console.log(ent.hp)
                ctx.fillRect(ent.pos.x - Camera.pos.x + window.innerWidth / 2 + hpbaroffset.x,
                    ent.pos.y - Camera.pos.y + window.innerHeight / 2 + hpbaroffset.y, 100 * ent.hp / 280, 10)
                ctx.strokeRect(ent.pos.x - Camera.pos.x + window.innerWidth / 2 + hpbaroffset.x,
                    ent.pos.y - Camera.pos.y + window.innerHeight / 2 + hpbaroffset.y, 100, 10)
            }
        }
    }

    function shot1() {
        ws.sendRequest("makeShot", 1)
    }
    
    function shot2() {
        ws.sendRequest("makeShot", 2)
    }

    function renderButtons() {
        let center = 0.5 * canv.width
        let leftButtonCoords = {
            x: center - 0.05 * canv.width,
            y: 0.8 * canv.height
        }
        ctx.drawImage(sprites.buttonL, leftButtonCoords.x, leftButtonCoords.y, 60, 60)
        ctx.drawImage(sprites.buttonR, leftButtonCoords.x + 0.1 * canv.width, leftButtonCoords.y, 60, 60)
        /*ctx.lineWidth = 5
        ctx.strokeStyle = "black"
        ctx.fillStyle = "white"*/

        //console.log(lastMousePosition, leftButtonCoords)
        //console.log(highlight)
        /*if (lastMousePosition.x >= leftButtonCoords.x && lastMousePosition.x <= leftButtonCoords.x + 60 &&
            lastMousePosition.y >= leftButtonCoords.y && lastMousePosition.y <= leftButtonCoords.y + 60) {
            ctx.fillStyle = "rgb(100, 100, 100)"
        }

        if(highlight.left) {
            ctx.fillStyle = "red"
            setTimeout(() => {
                highlight.left = false
            }, 100)
        }*/
        /*ctx.fillRect(leftButtonCoords.x, leftButtonCoords.y, 60, 60)
        ctx.strokeRect(leftButtonCoords.x, leftButtonCoords.y, 60, 60)


        //console.log(lastMousePosition, leftButtonCoords.x + 0.1 * canv.width)
        ctx.fillStyle = "white"
        if (lastMousePosition.x >= leftButtonCoords.x + 0.1 * canv.width && lastMousePosition.x <= leftButtonCoords.x + 60 + 0.1 * canv.width &&
            lastMousePosition.y >= leftButtonCoords.y && lastMousePosition.y <= leftButtonCoords.y + 60) {
            ctx.fillStyle = "rgb(100, 100, 100)"
        }
        
        if (highlight.right) {
            ctx.fillStyle = "red"
            setTimeout(() => {
                highlight.right = false
            }, 100)
        }

        ctx.fillRect(leftButtonCoords.x + 0.1 * canv.width, leftButtonCoords.y, 60, 60)
        ctx.strokeRect(leftButtonCoords.x + 0.1 * canv.width, leftButtonCoords.y, 60, 60)*/
        
    }

    function renderMinimap() {
        ctx.strokeStyle = "black"
        ctx.lineWidth = 10
        ctx.fillStyle = "#4169E1"
        let size = 0.2 * canv.width

        ctx.fillRect(0, canv.height - size, size, size)
        ctx.strokeRect(0, canv.height - size, size, size)

        // let myTeam = entities.filter(ent => ent.team == playerTeam)
        let myTeam = entities.filter(ent => ent.type === "Player")

        let vision = []
        //ctx.transform(1, 0, 0, -1, 0, canv.height)
        for (let ent of myTeam) {
            let mapCoords = {
                x: (ent.pos.x + mapSize.x) / mapSize.x * size / 2,
                y: (ent.pos.y + mapSize.y) / mapSize.y * size / 2
            }
            vision.push(mapCoords)
            ctx.fillStyle = "rgb(135, 206, 235)"
            ctx.beginPath()
            ctx.ellipse(mapCoords.x, mapCoords.y + canv.height - size, 40, 40, 0, 0, Math.PI * 2)
            ctx.fill()
        }


        for (let ent of entities) {
            let mapCoords = {
                x: (ent.pos.x + mapSize.x) / mapSize.x * size / 2,
                y: (ent.pos.y + mapSize.y) / mapSize.y * size / 2
            }
            let good = vision.filter(coord => distance(mapCoords, coord) <= 40)
            if (good.length > 0) {
                ctx.save()
                ctx.translate(mapCoords.x, mapCoords.y + canv.height - size)
                ctx.rotate(ent.angle)

                ctx.fillStyle = ent.team
                ctx.strokeStyle = "black"
                ctx.lineWidth = 1
                ctx.fillRect(-5, -5, ent.size.x / 10, ent.size.y / 10)
                ctx.strokeRect(-5, -5, ent.size.x / 10, ent.size.y / 10)
                ctx.restore()
            }
        }
    }

    function renderGold() {
        ctx.strokeStyle = "black"
        ctx.lineWidth = 10
        ctx.fillStyle = "rgb(100, 100, 100)"

        let menuCoords = {
            x: 0.8 * canv.width,
            y: 0.8 * canv.height
        }

        ctx.fillRect(menuCoords.x, menuCoords.y, 0.2 * canv.width, 0.2 * canv.height)
        ctx.strokeRect(menuCoords.x, menuCoords.y, 0.2 * canv.width, 0.2 * canv.height)

        ctx.fillStyle = "yellow" 
        ctx.font = "20px helvetica"
        let textCoords = {
            x: menuCoords.x + 0.025 * canv.width,
            y: menuCoords.y + 0.1 * canv.height
        }
        ctx.fillText("1488 g", textCoords.x, textCoords.y)
    }

    function render() {
        canv.width = window.innerWidth
        canv.height = window.innerHeight

        //ctx.transform()
        ctx.clearRect(0, 0, canv.width, canv.height)
        renderField()
        renderEntities()
        renderMinimap()
        renderButtons()
        renderGold()
    }

    window.onload = () => {
        init()
        setInterval(render, 17)
        setInterval(() => {
            offset *= -1
        }, 500)
    }
})();
