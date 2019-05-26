(

title: "Audio/control busses",

initModule: { | self |
},

makeBus: { | self, name, channels=2 |
	var myBus = self[name.asSymbol];
	if (myBus != nil, { myBus.free; });
	self[name.asSymbol] = Bus.audio(s,channels);
	self[name.asSymbol];
},

makeControlBus: { | self, name, channels=1 |
	var myBus = self[name.asSymbol];
	if (myBus != nil, { myBus.free; });
	self[name.asSymbol] = Bus.control(s,channels);
	self[name.asSymbol];
},

removeBus: { | self, name |
	var myBus = self[name.asSymbol];
	myBus.free;
	self.removeAt(name.asSymbol);
},

)




