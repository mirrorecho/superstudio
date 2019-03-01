(

title: "Synth Helper",

initModule: { | self |
	self.libraryPath = ~ss.path ++ "ss.synther-libraries/";
	self.loadLibrary("common");
},

makeSynth: { arg self, name, synthName, args=[];
	var mySynth = self[name.asSymbol];
	if (mySynth != nil, { mySynth.free; });
	if (synthName == nil, {synthName=name;});
	self[name.asSymbol] = Synth(synthName, args);
	self[name.asSymbol];
},

// TO DO, the following modules could be generalized into a library util for synther, sampler, and arrange

loadLibrary: { arg self, libraryName;
	(self.libraryPath ++ libraryName ++ ".sc").load;
	~ss.postPretty(["Loaded synther library: '" ++ libraryName ++ "'"]);
},

openAllLibraries: { arg self;
	self.libraryPaths.do{|p|p.openDocument;};
},

libraryPaths: { arg self;
	(self.libraryPath ++ self.name ++ ".*.sc").pathMatch;
},

)
