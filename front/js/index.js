(function () {
    class Entity {
        constructor(pos, size, angle, id, team) {
            this.pos = pos
            this.size = size
            this.angle = angle
            this.sprite = new Image(this.size.x, this.size.y)
            this.sprite.src = "img/ship.png"
            this.id = id
            this.team = team
        }
    }

    var Camera = {
        pos: {
            x: 0,
            y: 0
        },
    }

    var canv = document.getElementById("canv")
    var ctx = canv.getContext("2d")
    var entities = []
    var playerId = 0
    var playerTeam = "blue"
    var lastMousePosition = {
        x: 0, y: 0
    }

    function init() {
        entities.push(new Entity({ x: 0, y: 0 }, { x: 200, y: 100 }, Math.PI / 4, 0, "blue"))
        entities.push(new Entity({ x: -500, y: -500 }, { x: 200, y: 100 }, 0, 1, "blue"))
        entities.push(new Entity({ x: -500, y: 500 }, { x: 200, y: 100 }, Math.PI / 2, 2, "red"))
        entities.push(new Entity({ x: -500, y: -700 }, { x: 200, y: 100 }, Math.PI / 2, 2, "red"))

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
                alert('left button')
            }


            if (lastMousePosition.x >= leftButtonCoords.x + 0.1 * canv.width && lastMousePosition.x <= leftButtonCoords.x + 60 + 0.1 * canv.width &&
                lastMousePosition.y >= leftButtonCoords.y && lastMousePosition.y <= leftButtonCoords.y + 60) {
                alert('right button')
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
        ctx.drawImage(image, -(image.width / 2), -(image.height / 2), sizeX, sizeY)
        ctx.restore()
    }

    function renderField() {
        ctx.fillStyle = "rgb(0, 0, 100)"
        ctx.fillRect(0, 0, canv.width, canv.height)
    }

    function renderEntities() {
        let you = entities.filter(ent => ent.id == playerId)[0]

        Camera.pos = Object.assign({}, you.pos)

        for (let ent of entities) {
            if (ent.pos.x >= (Camera.pos.x - window.innerWidth / 2 - max(ent.size.x, ent.size.y)) && ent.pos.x <= (Camera.pos.x + window.innerWidth / 2 + max(ent.size.x, ent.size.y))
                && ent.pos.y >= (Camera.pos.y - window.innerHeight / 2 - max(ent.size.x, ent.size.y)) && ent.pos.y <= (Camera.pos.y + window.innerHeight / 2 + max(ent.size.x, ent.size.y))) {
                drawRotatedImage(ent.sprite,
                    ent.pos.x - Camera.pos.x + window.innerWidth / 2,
                    ent.pos.y - Camera.pos.y + window.innerHeight / 2, ent.size.x, ent.size.y, ent.angle)
            }
        }
    }

    function renderButtons() {
        let center = 0.5 * canv.width
        let leftButtonCoords = {
            x: center - 0.05 * canv.width,
            y: 0.8 * canv.height
        }
        ctx.lineWidth = 5
        ctx.strokeStyle = "black"
        ctx.fillStyle = "white"

        //console.log(lastMousePosition, leftButtonCoords)
        if (lastMousePosition.x >= leftButtonCoords.x && lastMousePosition.x <= leftButtonCoords.x + 60 &&
            lastMousePosition.y >= leftButtonCoords.y && lastMousePosition.y <= leftButtonCoords.y + 60) {
            ctx.fillStyle = "red"
        }
        ctx.fillRect(leftButtonCoords.x, leftButtonCoords.y, 60, 60)
        ctx.strokeRect(leftButtonCoords.x, leftButtonCoords.y, 60, 60)


        //console.log(lastMousePosition, leftButtonCoords.x + 0.1 * canv.width)
        ctx.fillStyle = "white"
        if (lastMousePosition.x >= leftButtonCoords.x + 0.1 * canv.width && lastMousePosition.x <= leftButtonCoords.x + 60 + 0.1 * canv.width &&
            lastMousePosition.y >= leftButtonCoords.y && lastMousePosition.y <= leftButtonCoords.y + 60) {
            ctx.fillStyle = "red"
        }

        ctx.fillRect(leftButtonCoords.x + 0.1 * canv.width, leftButtonCoords.y, 60, 60)
        ctx.strokeRect(leftButtonCoords.x + 0.1 * canv.width, leftButtonCoords.y, 60, 60)
    }

    function renderMinimap() {
        ctx.strokeStyle = "black"
        ctx.lineWidth = 10
        ctx.fillStyle = "#4169E1"
        let size = 0.2 * canv.width

        ctx.fillRect(0, canv.height - size, size, size)
        ctx.strokeRect(0, canv.height - size, size, size)

        let myTeam = entities.filter(ent => ent.team == playerTeam)
        for (let ent of myTeam) {
            let mapCoords = {
                x: (ent.pos.x + 1000) / 1000 * size / 2,
                y: (ent.pos.y + 1000) / 1000 * size / 2
            }
            ctx.fillStyle = "rgb(135, 206, 235)"
            ctx.beginPath()
            ctx.ellipse(mapCoords.x, mapCoords.y + canv.height - size, 40, 40, 0, 0, Math.PI * 2)
            ctx.fill()
        }


        for (let ent of entities) {
            let mapCoords = {
                x: (ent.pos.x + 1000) / 1000 * size / 2,
                y: (ent.pos.y + 1000) / 1000 * size / 2
            }
            ctx.save()
            ctx.translate(mapCoords.x, mapCoords.y + canv.height - size)
            ctx.rotate(ent.angle)

            ctx.fillStyle = ent.team
            console.log(ctx.fillStyle, ent.team)
            ctx.strokeStyle = "black"
            ctx.lineWidth = 1
            ctx.fillRect(-5, -5, ent.size.x / 10, ent.size.y / 10)
            ctx.strokeRect(-5, -5, ent.size.x / 10, ent.size.y / 10)
            ctx.restore()
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
        ctx.font = "20px serif"
        let textCoords = {
            x: menuCoords.x + 0.025 * canv.width,
            y: menuCoords.y + 0.1 * canv.height
        }
        ctx.fillText("1488 g", textCoords.x, textCoords.y)
    }

    function render() {
        canv.width = window.innerWidth
        canv.height = window.innerHeight

        ctx.clearRect(0, 0, canv.width, canv.height)
        renderField()
        renderEntities()
        renderMinimap()
        renderButtons()
        renderGold()
    }

    init()
    setInterval(render, 17)
})();