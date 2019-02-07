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

	makeCopy: { arg self, eArgs=(); ().putAll(self).putAll(eArgs); },

	makeModule: { arg self, moduleName, eModule=();
		self[moduleName.asSymbol] = self.ss.protoModule.makeCopy.putAll(eModule);
		self[moduleName.asSymbol].name = moduleName;
		self[moduleName.asSymbol].initModule.value;
		self[moduleName.asSymbol];
	},

	// postln: { | self | "MA"},

	// TO CONSIDER.. loadExtend?

	load: { arg self, modules=[], callback={}, path;
		var eModule;
		{
			s.sync;
			modules.do { arg moduleName;
				(path ?? (self.ss.path ++ "modules/") ++ moduleName ++ ".sc").postln;
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

~ss = protoModule.makeCopy;
protoModule.ss = ~ss;
~ss.protoModule = protoModule;
~ss.ss = ~ss;
~ss.name = "ss";
~ss.title = "Super Studio";

~ss.putAll((

	initialized: false,

	path: myPath,

	projectPath: myPath, // will typically replace with project specific path

	start: {arg self, callback={};
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

~ss.start;

)


