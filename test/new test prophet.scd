p = Prophet08("Keyboard Uno", "USB Uno MIDI Interface"); c = p.gui;


~data.postcs
(
~data = Int8Array[ -16, 1, 35, 3, 0, 24, 49, 3, 9, 1, 24, 52, 0, 3, 12, 1, 0, 0, 5, 40, 64, 0, 64, 4, 11, 0, 1, 12, 0, 15, 0, 0, 51, 102, 43, 0, 0, 0, 127, 127, 127, 0, 0, 55, 0, 74, 84, 41, 0, 0, 3, 1, 0, 17, 0, 3, 2, 0, 12, 0, 0, 3, 1, 0, 10, 0, 127, 11, 0, 0, 9, 127, 0, 0, 0, 83, 72, 0, 127, 13, 62, 9, 13, 26, 32, 19, 13, 107, 26, 13, 23, 37, 0, 0, 0, 0, 0, 127, 0, 127, 0, 0, 127, 0, 127, 0, 127, 0, 0, 120, 2, 12, 0, 2, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 57, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 68, 101, 115, 116, 114, 0, 117, 99, 116, 105, 111, 110, 32, 0, 71, 101, 110, 116, 12, 49, 2, 0, 0, 1, 24, 51, 1, 0, 1, 0, 0, 0, 2, 41, 0, 0, 97, 8, 19, 127, 1, 47, 0, 0, 30, 0, 42, 93, 33, 0, 0, 127, 127, 0, 0, 0, 0, 64, 64, 65, 84, 0, 0, 0, 3, 1, 80, 0, 0, 0, 0, 0, 80, 0, 0, 0, 0, 64, 80, 0, 0, 0, 0, 3, 126, 0, 0, 0, 0, 35, 0, 0, 13, 9, 29, 19, 13, 89, 9, 0, 127, 0, 0, 0, 127, 0, 0, 0, 0, 0, 0, 127, 0, 127, 0, 127, 0, 0, 127, 0, 127, 0, 120, 2, 4, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 60, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -9 ];
)

p.midiController.prReceiveSysex(~data)

(
var data = ~data;
if (data[0..3] == Int8Array[0xF0, 0x01, 0x23, 0x03]) {
  // Program Edit Buffer Data Dump
  data = data[4..(data.size - 2)];
  if (data.size != 439) { "Wrong data size".error };
  data = data.collect(_.asBinaryDigits).clump(8).collect { |packet|
    var msBits = packet[0];
    if (msBits[0] != 0) { "Received bad sysex".error };
    packet = packet[1..].collect { |bits, i|
      if (bits[0] != 0) { "Received bad sysex".error };
      bits[0] = msBits[7 - i];
      bits.convertDigits(2);
    };
  };
  //p.midiController.midiOn = false;
  data.flat.do { |value, num|
    var param = p[num];
    //[param, num, value].postln;
    if (param.notNil) {
      //[param, param.midiValue, value].postln;
      param.midiValue = value;
    };
  };
  //p.midiController.midiOn = true;
};
)

(
p.midiController.outDevice.sysex(Int8Array[0xF0, 0x01, 0x23, 0x06, 0xF7]);

MIDIdef.sysex(\testRcv, { |data|
  ~data = data;
  if (data[0..3] == Int8Array[0xF0, 0x01, 0x23, 0x03]) {
    // Program Edit Buffer Data Dump
    data = data[4..(data.size - 2)];
    data = data.collect(_.asBinaryDigits).clump(8).collect { |packet|
      var msBits = packet[0];
      if (msBits[0] != 0) { "Received bad sysex".error };
      packet = packet[1..].collect { |bits, i|
        if (bits[0] != 0) { "Received bad sysex".error };
        bits[0] = msBits[7 - i];
        bits.convertDigits(2);
      };
    };
    p.midiController.midiOn = false;
    data.flat.do { |value, num|
      var param = p[num];
      //[param, num, value].postln;
      if (param.notNil) {
        [param, value].postln;
        //param.midiValue = value;
      };
    };
    //p.midiController.midiOn = true;
  };
});

//MIDIdef.sysex(\testRcv, {});
)

p[37].midiValue = 40

5.asBinaryDigits.convertDigits(2)

p[5]
p[5] = 0


p.midiController
p.layerA.arpMode
p.layerA.osc1.freq = 90
p.layerA.osc2.fine = 15
p.layerA.osc1.keyboardOn = false
p.layerA.filter.freq = 50
p.layerA.slop = 5
p.layerA.seq1[0] = 20
p.layerA.seq1[1] = 50
p.layerA.seq2 = (0..10)
p.layerA.seq3 = (5..25)

p.layerA.unisonMode = 0

p.layerA.footCtrl.amt.value
p.layerA.modCtrl


c.view.oscView.noise.addDependant({ |...args| args.postcs; })

c.view.ids[100].addDependant({ |...args| args.postln})

p.layerA.seq1Dest = 8
p.layerA.seq2Dest = 4

c.view.ctrlView.amts[4].defaultValue = 0