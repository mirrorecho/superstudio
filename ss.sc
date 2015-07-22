/*
TO DO!
 - tempo clock module
 - Pbind factory module?
*/

(

~ss = Environment.make;
~ss.know = true;
~ss.modules=[];
~ss.path = "".resolveRelative; // funny, doesn't work if this is current file open in sublime text

~ss.makeModule = { arg ss, name, namespace, title, function;
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
            (~ss.path ++ "/modules/" ++ module ++ ".sc").loadPaths;
            s.sync;
            ("Loaded Super Studio Module: '" ++ module ++ "'").postln;
        };
        callback.value;
    }.fork;
};

~ss.start = {arg ss, callback={};
    s.freeAll;
    Server.all.do(Buffer.freeAll);
    s.newAllocators; // new allocators (numbers) for busses, buffers, etc.
    ss.load(["core"], callback);
};

~ss.loadCommon = { arg ss, 
    callback = {};
    ~ss.load(["bus","master","midi","synth.library","buf"], callback);
};

)