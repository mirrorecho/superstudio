/*
TO DO!
- more cool synths
- - - drone makerfds
- - - swish, swell
- - - swish effects
- - - ghost sound
- - - simple drums
- tempo clock module?
*/

(

var myPath = "".resolveRelative;

// DEFINES protoModule as an event with standard methods for any super studio module
var protoModule = (
	name: "YO?",
	title: "A super studio module",
	initModule: { | self | }, // hook for function to initialize module

	getCopy: { arg self, name, eValues=();
		var myCopy = ().putAll(self).putAll(eValues);
		if (name!=nil, {self.name=name;});
		myCopy;
	},

	makeCopy: { arg self, name, eValues=();
		self[name.asSymbol] = self.getCopy(name, eValues);
		self[name.asSymbol];
	},

	getModule: { arg self, name, eValues=();
		var myModule = self.ss.protoModule.getCopy(name).putAll(eValues);
		myModule.name=name;
		myModule.initModule.value;
		myModule;
	},

	makeModule: { arg self, name, eValues=();
		self[name.asSymbol] = self.getModule(name, eValues);
		self[name.asSymbol];
	},

	getModuleList: { arg self, name, list=[], eValues=();
		var myModule = self.getModule(name, eValues);
		myModule.listSize = list.size;
		myModule.nameList = list.size.collect{|i| list[i].name.asSymbol;};
		list.do{|e| myModule[e.name.asSymbol] = e;};

		myModule.byIndex = {arg myModule, index;
			myModule[myModule.nameList[index]];
		};

		myModule.list = {arg myModule; myModule.listSize.collect{|i| myModule.byIndex(i);}};

		myModule.getCopy = { arg myModule, name, eListValues=(), eValues=();
			var myCopy = ().putAll(myModule).putAll(eValues);
			if (name!=nil, {myCopy.name=name;});
			myCopy.list.do{|e|
				// e.name.postln;
				myCopy[e.name.asSymbol] = myCopy[e.name.asSymbol].getCopy(e.name, eListValues[e.name.asSymbol])
			};
			myCopy;
		};

		myModule.makeCopy = {  arg myModule, name, eListValues=(), eValues=();
			self[name.asSymbol] = myModule.getCopy(name, eListValues=(), eValues=());
		};
		myModule;
	},

	makeModuleList: { arg self, name, list=[], eValues=();
		self[name.asSymbol] = self.getModuleList(name, list, eValues);
		self[name.asSymbol];
	},


	load: { arg self, modules=[], callback={}, path;
		var eModule;
		{
			s.sync;
			modules.do { arg moduleName;
				(path ?? (self.ss.
					path ++ "modules/") ++ moduleName ++ ".sc").postln;
				eModule = (path ?? (self.ss.path ++ "modules/") ++ moduleName ++ ".sc").load;
				self.makeModule(moduleName, eModule);
				s.sync;
				("Loaded module: '" ++ moduleName ++ "'").postln;
			};
			callback.value;

		}.fork;
	},

	loadLocal: { arg self, modules=[], callback={};
		self.load(modules, callback, self.ss.projectPath);
	},
);

~ss = protoModule.makeCopy("ss");
protoModule.ss = ~ss; // NEEDED???
~ss.protoModule = protoModule; // NEEDED???
~ss.ss = ~ss; // NEEDED???
~ss.title = "Super Studio";

~ss.putAll((

	initialized: false,

	path: myPath,

	projectPath: myPath, // will typically replace with project specific path

	startServer: {arg self, callback={};
		var ssRecycle = {
			s.freeAll;
			Server.all.do(Buffer.freeAll); // necessary even with reboot?
			s.newAllocators; // new allocators (numbers) for busses, buffers, etc.
			// s.killAll;
			self.initialized = true;
		};
		if ( self.initialized != true, {ServerBoot.add(ssRecycle, \default);} );
		s.reboot;
	},

	loadCommon: { arg self, callback={};
		self.load(["bus","master","synth.library","buf"], callback);
	},


	postPretty: { arg self, msgs=[""];
		"-----------------------".postln;
		msgs.do {arg msg; msg.postln; };
		" ".postln;
	},

));

~ss.startServer;

)




