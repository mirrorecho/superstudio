(

var ssPath = "".resolveRelative;

// DEFINES protoModule as an event with standard methods for any super studio module
var protoModule = (
	name: "YO?",
	title: "A super studio module",
	path: ssPath,
	initModule: { | self | }, // hook for function to initialize module

	getCopy: { arg self, name, eValues=();
		var myCopy = ().putAll(self).putAll(eValues);
		if (name!=nil, {myCopy.name=name;});
		myCopy;
	},

	makeCopy: { arg self, name, eValues=();
		self[name.asSymbol] = self.getCopy(name, eValues);
		self[name.asSymbol];
	},

	getModule: { arg self, name, eValues=();
		var myModule = ~ss.protoModule.getCopy(name).putAll(eValues);
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

	loadModule: { arg self, name, path, prefix, callback={};
		var modulePath = (path ?? self.path);
		var moduleFullName = (prefix ?? "") ++ name;
		var moduleFilePath = modulePath ++ moduleFullName ++ ".sc";
		var eModule = (moduleFilePath).load;
		eModule.path = path;
		eModule.filePath = moduleFilePath;
		self.makeModule(name, eModule);
		s.sync;
		~ss.postPretty(["Loaded module: '" ++ moduleFullName ++ "'"]);
		callback.value;
		eModule;
	},

	load: { arg self, modules=[], callback={};
		{
			s.sync;
			modules.do { arg moduleName;
				self.loadModule(moduleName, self.path, self.name ++ ".");
			};
			callback.value;
		}.fork;
	},


	openAll: { arg self;
		self.filePath.openDocument;
		self.subPaths.do{|p|p.openDocument;};
	},

	subPaths: { arg self;
		(self.path ++ self.name ++ ".*.sc").pathMatch;
	},

);


~ss = protoModule.getCopy("ss", (

	protoModule: protoModule,

	title: "Super Studio!",

	filePath: ssPath ++ "ss.sc",

	initialized: false,

	modules: ["bus", "synther", "master", "buf", "midi", "arrange", "sampler"],

	open: { arg self, subPath;
		(~ss.path ++ subPath).openOS;
	},

	startServer: {arg self, callback={};
		var ssRecycle;
		ServerBoot.removeAll;
		ssRecycle = {
			s.freeAll; // necessary even with reboot?
			Server.all.do(Buffer.freeAll); // necessary even with reboot?
			s.newAllocators; // new allocators (numbers) for busses, buffers, etc.
			// s.killAll;
			self.initialized = true;
		};
		if ( self.initialized != true, {ServerBoot.add(ssRecycle, \default);} );
		s.reboot;
	},

	initModule: { arg self, callback={};
		CmdPeriod.removeAll;
		CmdPeriod.add({
			self.load(self.modules, callback);
		});
		CmdPeriod.run;
	},

	startup: { arg self, callback={};

	},


	postPretty: { arg self, msgs=[""];
		"-----------------------".postln;
		msgs.do {arg msg; msg.postln; };
		" ".postln;
	},

)

);

~ss.startServer;

)




