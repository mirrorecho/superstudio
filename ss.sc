/*
TO DO!
 - more cool synths
 - - - drone maker
 - - - swish, swell
 - - - swish effects
 - - - ghost sound
 - - - simple drums
 - tempo clock module?
 - Pbind/Proxy factory module?
 - KISS
 - module to work with Max/MSP easily
*/

(
var initialized = false;
if ( currentEnvironment.includesKey(\ss) , {initialized = ~ss.initialized;});
~ss = Environment.make;
~ss.know = true;
~ss.modules=[];
~ss.path = "".resolveRelative; // funny, doesn't work if this is current file open in sublime text
~ss.projectPath = ~ss.path; // will typically replace with project specific path
~ss.initialized = initialized; // set to true once ~ss first initialized (since some setup changes if the following code block called 2nd time)

~ss.makeModule = { arg ss, name, namespace=[], title="", function={arg ss, module; };
    var moduleNamespace = ss;
    namespace.do { arg namespaceLevel;
        if (moduleNamespace[namespaceLevel.asSymbol] == nil, {
                moduleNamespace[namespaceLevel.asSymbol] = Environment.make;
                moduleNamespace[namespaceLevel.asSymbol].know = true;
                moduleNamespace[namespaceLevel.asSymbol].title = title;
            }
        );
        moduleNamespace = moduleNamespace[namespaceLevel.asSymbol];
    };

    function.value(ss:ss, module:moduleNamespace);

    ss.modules = ss.modules ++ [name];


};

~ss.load = { arg ss, modules=["core"], callback={};
    {
        s.sync;
        modules.do { arg module;
            (ss.path ++ "/modules/" ++ module ++ ".sc").loadPaths;
            s.sync;
			("Loaded Super Studio Module: '" ++ module ++ "'").postln;
        };
        callback.value;
    }.fork;
};

~ss.loadLocal = { arg ss, modules=[], callback={};
    {
        s.sync;
        modules.do { arg module;
            (ss.projectPath ++ module ++ ".sc").loadPaths;
            s.sync;
			("Loaded Local Project Module: '" ++ module ++ "'").postln;
        };
        callback.value;
    }.fork;
};

~ss.start = {arg ss, callback={};
	f = {
		s.freeAll;
		Server.all.do(Buffer.freeAll); // necessary even with reboot?
		s.newAllocators; // new allocators (numbers) for busses, buffers, etc.
		ss.load(["core"], callback);
		ss.initialized = true;
	};
	postln(ss.initialized);
	if ( ss.initialized != true, {ServerBoot.add(f, \default);} );
	s.reboot;
};

~ss.loadCommon = { arg ss, callback={};
	~ss.load(["bus","master","synth.library","buf"], callback); // note: removed "midi" from list
};

~ss.start;

)