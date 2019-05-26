(

title: "OSC Helper",

initModule: { arg self;
	self.receiveIP = "127.0.0.1";
	self.port = 8000;
},

listen: { arg self;

},


addBusListener: { arg self, name=\listenBus1, args=(busName:"osc1", path:"/streamo", msgSelect:2);
	var myBus = ~ss.bus.makeControlBus(args.busName);
	{
		s.sync; // NECESSARY?

		self[name.asSymbol] = OSCdef(name, func:{arg msg;
			// msg.postln;
			myBus.value = msg[args.msgSelect];
		}, path:args.path, recvPort:self.port);
	}.fork;
},

addFuncListener: { arg self, name=\listenFunc1, args=(func:{|msg, time, addr, recvPort| msg.postln;}, path:"/streamo");
	self[name.asSymbol] = OSCdef(name, func:args[\func], path:args.path, recvPort:self.port);
},

removeListener: { arg self, name;
	self[name.asSymbol].free;
	self.removeAt(name.asSymbol);
},

)



//
// (
// { In.kr(~controlBus).poll }.play; // to see the
// )
//


