(
var device = ["Keyboard Uno", "USB Uno MIDI Interface"];
//device = nil;
MIDIIn.connectAll;
p = Prophet8(*device);
EZMIDI.keyboard(nil, p.outDevice);
)

p.addDependant { |...args| args.postln }

p.layerA.osc2.freq = 36
p.layerA.oscs.do(_.glide = 0)

(
k = MIDIIn.findPort("microKEY2", "KEYBOARD");
MIDIdef.noteOn(\korgOn, { |vel, num|
  p.outDevice.noteOn(0, num, vel);
}, srcID: k.uid);
MIDIdef.noteOff(\korgOff, { |vel, num|
  p.outDevice.noteOff(0, num, vel);
}, srcID: k.uid);
MIDIdef.cc(\korgCC, { |val, num|
  p.outDevice.control(0, num, val);
}, srcID: k.uid);
MIDIdef.bend(\korgBend, { |val|
  p.outDevice.bend(0, val);
}, srcID: k.uid);
)

switch(0, 1, {5}, 0, {6})


p.layerA.osc1.freq = 80
p.layerA.osc2.freq = 37
p.layerA.osc2.shape = 1
p.layerB.osc1.freq = 12
p.layerB.osc2.freq = 36
p.layerB.osc2.shape = 1
p.layerA.noiseAmt = 127
p.layerA.footCtrl.dest = 1





p.layerA.osc1.glide

p.prSendParam(15, 164)

(
r.stop; r = fork {
  var stream = Pbrown(0, 127, 10).asStream;
  loop {
    p.prSendParam(15, stream.next);
    0.1.wait;
  }
}
)
r.stop

// 95 and 96 are swapped from manual!!
p.prSendParam(15, 164)

SCDoc.indexAllDocuments(true)

p.layerA.osc1.glide = 0
p.voice.splitPoint = 0
p.layerA.clockDivide = 9



(4.collect { |n| 16.collect { |i| ("seq" ++ (n + 1) ++ "Step" ++ i).asSymbol } }).flat

