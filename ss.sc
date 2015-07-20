// TO DO.. PREVENT BUSSES FROM BEING REPEATEDLY RE-ALLOCATED AND USED UP

(

~ss = Environment.make;
~ss.know = true;
~ss.modules=[];


~ss.loadModule = { arg ss, name, namespace, dependencies, title, function;
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

    {
        function.value(ss:ss, module:moduleNamespace);
        s.sync;
    }.fork;

    ss.modules = ss.modules ++ [name];
    ("Loaded SuperStudio Module:" + title).postln;

};



~ss.load = { arg ss, 
    ssPath = "/home/randall/Code/mirrorecho/superstudio", // how to avoid hard-coding this?
    modules=["core","synth","bus","master","midi","synth.library","buf"];

    modules.do { arg module;
        (ssPath ++ "/modules/" ++ module ++ ".sc").loadPaths;
    };

};

)