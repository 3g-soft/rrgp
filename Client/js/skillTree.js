class SkillTree {
    constructor(skills) {
        document.getElementById('skillbtn').onclick = (() => this.toggle()).bind(this);
        this.tree = document.getElementById('tree');
        let self = this;
        this.showed = true;
        this.hotNodes = [];
        this.root = new Node(undefined, undefined, [], undefined);
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
                let nd = new Node(node, text, [], parent);
                parent.nodes.push(nd);
                createNode(sk[i].childs, childs, nd);
                text.onclick = (e) => {
                    let target = undefined;
                    self.currentNode.nodes.forEach((it) => {
                        if (it.text === e.originalTarget) {
                            target = it;
                        }
                    });
                    if (target === undefined) return;
                    self.currentNode = target;
                    target.activate();
                }
            }
        }
        createNode(skills, this.tree, this.root)
        this.root.activate();
        window.addEventListener("keydown", ((e) => {
            switch (e.key) {
                case 'i': this.toggle(); break;
                case '1': this.nodeBykey(0); break;
                case '2': this.nodeBykey(1); break;
            }
        }).bind(this));
    }

    nodeBykey(i) {
        if (!this.showed) return;
        this.currentNode.nodes[i].activate();
        this.currentNode = this.currentNode.nodes[i]
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
        let out = Array.from(str.matchAll(/[+-][^+-]*/gm), m => m[0]);
        return out.join("<br>");
    }

}

class Node {
    constructor(node, text, nodes, parent) {
        this.text = text;
        this.nodes = nodes;
        this.parent = parent;
        this.node = node;
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
        name: "+SPEED +RANGE -HP",
        childs: [{
            name: "+DAMAGE -RELOAD",
            childs: [
                { name: "+TURN -RELOAD" },
                { name: "+DAMAGE -HP" }
            ]
        },
        {
            name: "+SPEED -DAMAGE",
            childs: [
                { name: "+SPEED -HP" },
                { name: "+TURN -DAMAGE" }
            ]
        }
        ]
    },
    {
        name: "+BODY DAMAGE +SPEED -DAMAGE",
        childs: [{
            name: "+BODY DAMAGE -TURN",
            childs: [
                { name: "+SPEED -DAMAGE" },
                { name: "+BODY DAMAGE -DAMAGE" }
            ]
        },
        {
            name: "+SPEED -TURN",
            childs: [
                { name: "+BODY DAMAGE -TURN" },
                { name: "+SPEED -RELOAD" }
            ]
        }
        ]
    },
];
let st = new SkillTree(skills);
st.hide();
console.log(st);