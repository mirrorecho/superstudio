(

title: "Synth Helper",

initModule: { | self |
	self.libraryPath = ~ss.path ++ "ss.synther-libraries/";
	self.loadLibrary("common");
},

makeSynth: { arg self, name, synthDefName, args=[];
	var mySynth = self[name.asSymbol];
	if (mySynth != nil, { mySynth.free; });
	if (synthDefName == nil, {synthDefName=name;});
	self[name.asSymbol] = Synth(synthDefName, args);
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
