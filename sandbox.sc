(
// RUN FIRST:
// ... update the following to local path of superstudio.sc
("/Users/rwest/Code/mirrorecho/superstudio/ss.sc").load;
)

~ss.openAll;

(
~ss.initModule({
	~ss.buf.libraryPath = "/Users/rwest/Echo/Sounds/Library/";
	~ss.buf.loadLibrary("japan-cicadas");
	~ss.buf.loadLibrary("shamisen");
	~ss.buf.loadLibrary("piano");
	~sandbox = ~ss.arrange.makeWork("sandbox");
});
)



