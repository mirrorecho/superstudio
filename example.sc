("/Users/randallwest/code/mirrorecho/superstudio/ss.sc").loadPaths;
// ~ss.start;


~ss.loadCommon;

(
p = PbindProxy.new;
p.set(*[instrument:"ss.spacey"]);
p.play;
)