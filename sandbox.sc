
(
// RUN FIRST:
// ... update the following to local path of superstudio.sc
("/Users/rwest/Code/mirrorecho/superstudio/ss.sc").load;
)


~ss.loadCommon;
~ss.buf.libraryPath = "/Users/rwest/Echo/Sound/Library/";
~ss.buf.loadLibrary("japan-cicadas");
~ss.buf.loadLibrary("shamisen");

~ss.buf.play



~ss.example.ex;s

~yo.yo1;


init


d = (a:"yoMama");


d.so = {};

e = ().putAll(d);

Event;

e.fun(~args);

j = (yo:"ha");
k = ().putAll(j);
j.fof="bar";
k;
l=k;
k.foo="foobar";
l;
l===k;

g = (

	yo:"HA",
	play: {"DADADADA".postln;}
)

g.load;

h = "sandbox_module.sc".loadRelative;
h;
h[0];
h[0].name;

p = nil;
p ?? "dfsf";
p = "a";

proto

