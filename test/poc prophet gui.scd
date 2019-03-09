( // 1
var bounds = Rect(0, 0, 1080, 530);

MIDIIn.connectAll;
~prophet = Prophet8();

if (~win.notNil) { ~win.close };

~win = Window("Prophet '08", bounds.resizeBy(60, 0)).front
.background_(Color.hsv(0.05, 0.9, 0.5));

~view = P08View(~win, bounds.moveBy(30, 0));
)

( // 2
~prophet.removeDependant(f);
f = { |prophet, address, value|
  var match;
  [address, value].postln;
  if ((match = address.findRegexp("^layerA/(.*)")).size > 0) {
    var address = match[1][1].postln;
    if ((match = address.findRegexp("^osc/(.*)")).size > 0) {
      var address = match[1][1].postln;
      ~view.oscView.perform(address.asSymbol).value_(value);
    };
    if ((match = address.findRegexp("^filter/(.*)")).size > 0) {
      var address = match[1][1].postln;
      ~view.filtView.perform(address.asSymbol).value_(value);
    };
    if ((match = address.findRegexp("^vca/(.*)")).size > 0) {
      var address = match[1][1].postln;
      ~view.ampView.perform(address.asSymbol).value_(value);
    };
    if ((match = address.findRegexp("^env3/(.*)")).size > 0) {
      var address = match[1][1].postln;
      ~view.env3View.perform(address.asSymbol).value_(value);
    };
    if ((match = address.findRegexp("^lfo/(\\d)/(.*)")).size > 0) {
      var address = match[2][1].post;
      var index = match[1][1].asInt.postln;
      ~view.lfoViews[index].perform(address.asSymbol).value_(value);
    };
    if ((match = address.findRegexp("^top/(.*)")).size > 0) {
      var address = match[1][1].postln;
      ~view.topPanel.perform(address.asSymbol).value_(value);
    };
    if ((match = address.findRegexp("^seq/(\\d)/step/(.*)")).size > 0) {
      var index = match[1][1].asInt.post;
      var address = match[2][1].asInt.postln;
      if (index == 0) {
        ~view.seqView.steps[address].value_(value);
      };
    };
    if ((match = address.findRegexp("^seq/(\\d)/dest")).size > 0) {
      var index = match[1][1].asInt.postln;
      if (index == 0) {
        ~view.seqView.dest.value_(value);
      };
    };
    if ((match = address.findRegexp("^mod/(\\d)/(.*)")).size > 0) {
      var address = match[2][1].post;
      var index = match[1][1].asInt.postln;
      ~view.modViews[index].perform(address.asSymbol).value_(value);
    };
    if ((match = address.findRegexp("^ctrl/(\\d)/(.*)")).size > 0) {
      var address = match[2][1].post;
      var index = match[1][1].asInt.postln;
      ~view.ctrlView.perform((address ++ "s").asSymbol)[index].value_(value);
    };
  };
};
~prophet.addDependant(f);
)


( // 3
~prophet.layerA.osc1.freq = 48;
~prophet.layerA.osc1.fine = 52;
~prophet.layerA.osc1.shape = 2;
~prophet.layerA.osc1.glide = 20;
~prophet.layerA.osc1.keyboardOn = 1;
~prophet.layerA.osc2.freq = 36;
~prophet.layerA.osc2.fine = 48;
~prophet.layerA.osc2.shape = 1;
~prophet.layerA.osc2.glide = 25;
~prophet.layerA.osc2.keyboardOn = 1;
~prophet.layerA.noiseAmt = 20;
~prophet.layerA.oscMix = 64;
~prophet.layerA.sync = 0;
~prophet.layerA.slop = 3;
~prophet.layerA.glideMode = 3;
~prophet.layerA.unisonOn = 1;

~prophet.layerA.filter.freq = 30;
~prophet.layerA.filter.res = 80;
~prophet.layerA.filter.keyboardAmt = 25;
~prophet.layerA.filter.mod = 8;
~prophet.layerA.filter.env.amt = 200;
~prophet.layerA.filter.env.velAmt = 55;
~prophet.layerA.filter.env.attack = 10;
~prophet.layerA.filter.env.decay = 30;
~prophet.layerA.filter.env.sustain = 80;
~prophet.layerA.filter.env.release = 100;
~prophet.layerA.filter.poles = 1;

~prophet.layerA.vca.env.amt = 50;
~prophet.layerA.vca.env.decay = 20;
~prophet.layerA.vca.env.sustain = 50;
~prophet.layerA.vca.env.release = 100;
~prophet.layerA.vca.env.velAmt = 100;
~prophet.layerA.vca.spread = 65;
~prophet.layerA.vca.volume = 100;

~prophet.layerA.env3.amt = 50;
~prophet.layerA.env3.attack = 30;
~prophet.layerA.env3.decay = 20;
~prophet.layerA.env3.sustain = 50;
~prophet.layerA.env3.release = 100;
~prophet.layerA.env3.velAmt = 100;
~prophet.layerA.env3RepeatOn = 1;
~prophet.layerA.env3Dest = 3;

~prophet.layerA.lfo1.freq = 80;
~prophet.layerA.lfo1.amt = 50;
~prophet.layerA.lfo1.shape = 2;
~prophet.layerA.lfo1.keySync = 1;
~prophet.layerA.lfo1.dest = 9;

~prophet.layerA.lfo2.freq = 20;
~prophet.layerA.lfo2.amt = 30;
~prophet.layerA.lfo2.dest = 19;

~prophet.layerA.bendRange = 12;
~prophet.layerA.unisonMode = 4;
~prophet.layerA.bpm = 120;
~prophet.layerA.clockDivide = 1;
~prophet.layerA.gatedSeqOn = 1;

~prophet.layerA.seq1.steps = [50, 100, 90, 80, 126] ++ (0!11);
~prophet.layerA.seq1Dest = 5;

~prophet.layerA.mod1.source = 13;
~prophet.layerA.mod1.dest = 14;
~prophet.layerA.mod1.amt = 200;

~prophet.layerA.mod2.source = 6;
~prophet.layerA.mod2.dest = 6;
~prophet.layerA.mod2.amt = 150;

~prophet.layerA.modCtrl.dest = 9;
~prophet.layerA.modCtrl.amt = 190;
~prophet.layerA.footCtrl.dest = 13;
~prophet.layerA.footCtrl.amt = 250;
)

String.newFrom($ .dup(20)).postcs