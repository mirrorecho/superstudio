(

title: "Synth Helper",

initModule: { | self |
	self.loadLibrary("common");
},

makeSynth: { arg self, name, synthName, args=[];
	var mySynth = self[name.asSymbol];
	if (mySynth != nil, { mySynth.free; });
	if (synthName == nil, {synthName=name;});
	self[name.asSymbol] = Synth(synthName, args);
	self[name.asSymbol];
},

loadLibrary: { arg self, libraryName;
	(~ss.path ++ "synther-libraries/ss.synther." ++ libraryName ++ ".sc").load;
	~ss.postPretty(["Loaded synther library: '" ++ libraryName ++ "'"]);
},

)
