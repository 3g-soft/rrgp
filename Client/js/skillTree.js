class SkillTree {
    constructor(skills, connection) {
        document.getElementById('skillbtn').onclick = (() => this.toggle()).bind(this);
        this.tree = document.getElementById('tree');
        let self = this;
        this.connection = connection;
        this.showed = true;
        this.hotNodes = [];
        this.root = new Node(undefined, undefined, [], undefined);
        this.gold = 0;
        this.resetted = false;
        this.currentNode = this.root;
        function createNode(sk, div, parent = undefined) {
            if (sk === undefined) return;
            for (let i = 0; i < sk.length; i++) {
                let node = document.createElement('div');
                node.className = "node";
                let text = document.createElement('div');
                text.className = "node-name";
                node.classList.add("unavailable");
                text.innerHTML = self.insertBRs(sk[i].name);
                let childs = document.createElement('div');
                childs.className = "nodes";
                div.appendChild(node);
                node.appendChild(text);
                node.appendChild(childs);
                let nd = new Node(node, text, [], parent, sk[i].id);
                parent.nodes.push(nd);
                createNode(sk[i].childs, childs, nd);
                text.onmousedown = (e) => {
                    if(self.gold < 1)return;
                    self.gold--;
                    let target = undefined;
                    self.currentNode.nodes.forEach((it) => {
                        if (it.text === e.currentTarget) {
                            target = it;
                        }
                    });
                    if (target === undefined) return;
                    self.currentNode = target;
                    target.activate();
                    self.connection.sendRequest("skill", self.currentNode.id);
                }
            }
        }
        createNode(skills, this.tree, this.root);
        this.root.activate();
        window.addEventListener("keydown", ((e) => {
            if(this.showed)e.preventDefault();
            switch (e.key) {
                case 'i': this.toggle(); break;
                case '1': this.nodeBykey(0); break;
                case '2': this.nodeBykey(1); break;
            }
        }).bind(this));
        this.hide();
    }

    nodeBykey(i) {
        if (!this.showed) return;
        if(this.gold < 1)return;
        this.gold--;
        this.currentNode.nodes[i].activate();
        this.currentNode = this.currentNode.nodes[i];
        this.connection.sendRequest("skill", this.currentNode.id);
    }
    show() {
        document.getElementById("screen").style.display = "flex"
        this.showed = true;
    }
    hide() {
        document.getElementById("screen").style.display = "none";
        this.showed = false;
    }
    toggle() {
        this.showed ? this.hide() : this.show();
    }

    insertBRs(str){
        if("fuck".matchAll === undefined)return str
        let out = Array.from(str.matchAll(/[+-][^+-]*/gm), m => m[0]);
        return out.join("<br>");
    }

    reset() {
        this.currentNode = this.root
        function disableNodes(nodes) {
            nodes.forEach((n) => {
                n.node.classList.remove("available");
                n.node.classList.remove("active");
                n.node.classList.add("unavailable");
                disableNodes(n.nodes)
            })
        }
        disableNodes(this.root.nodes)
        this.root.activate()
    }

}

class Node {
    constructor(node, text, nodes, parent, id=-1) {
        this.text = text;
        this.nodes = nodes;
        this.parent = parent;
        this.node = node;
        this.id = id;
    }

    enable() {
        this.node.classList.remove("unavailable");
        this.node.classList.add("available");
    }

    activate() {
        if (this.parent !== undefined) this.parent.nodes.forEach((it) => { it.disable() })
        if (this.node !== undefined) {
            this.node.classList.remove("unavailable");
            this.node.classList.add("active");
        }
        this.nodes.forEach((it) => { it.enable() })
    }

    disable() {
        this.node.classList.remove("available");
        this.node.classList.add("unavailable");
    }
}

let skills = [
    {
        name: "+SPEED +RANGE -HP", id: 1,
        childs: [{
            name: "+DAMAGE -RELOAD",  id: 3,
            childs: [
                { name: "+TURN -RELOAD", id: 4},
                { name: "+DAMAGE -HP", id: 5}
            ]
        },
        {
            name: "+SPEED -DAMAGE", id: 6,
            childs: [
                { name: "+SPEED -HP", id: 7 },
                { name: "+TURN -DAMAGE", id: 8 }
            ]
        }
        ]
    },
    {
        name: "+BODY DAMAGE +SPEED -DAMAGE", id: 2,
        childs: [{
            name: "+BODY DAMAGE -TURN",  id: 9,
            childs: [
                { name: "+SPEED -DAMAGE", id: 10},
                { name: "+BODY DAMAGE -DAMAGE", id: 11}
            ]
        },
        {
            name: "+SPEED -TURN", id: 12,
            childs: [
                { name: "+BODY DAMAGE -TURN", id: 13},
                { name: "+SPEED -RELOAD", id: 14}
            ]
        }
        ]
    },
];