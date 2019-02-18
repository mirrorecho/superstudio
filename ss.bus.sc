(

// namespace: [],

title: "Audio/control busses",

initModule: { | self |
},

makeBus: { | self, name, channels=2 |
	var myBus = self[name.asSymbol];
	if (myBus != nil, { myBus.free; });
	self[name.asSymbol] = Bus.audio(s,channels);
	self[name.asSymbol];
},

)




