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
            // console.log(msg.data);
            this.promiseControl[parseInt(resp.rid)](resp.response);

        }).bind(this);
    }

    sendRequest(oncomplete, name, ...args){
        console.log(name);
        let id = this.nextid;
        this.nextid++;
        let promise = new Promise(((resolve, reject) => {
            this.promiseControl[id] = resolve;
        }).bind(this));
        promise.then(oncomplete, null);
        this.ws.send(JSON.stringify({op: name, args: args, rid: id}))
    }
}