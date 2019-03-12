P08GUIController {
  var <model, <window, <view;
  classvar <layerParamKey, globalParamKey;

  // ugh this is definitely not the best way to do this
  *initClass {
    layerParamKey = [
     "osc/freq1", "osc/fine1", "osc/shape1", "osc/glide1", "osc/keyboardOn1",
     "osc/freq2", "osc/fine2", "osc/shape2", "osc/glide2", "osc/keyboardOn2",
     "osc/sync", "osc/glideMode", "osc/slop", "osc/mix", "osc/noise",
     "filter/freq", "filter/res", "filter/keyboardAmt", "filter/mod", "filter/poles",
     "filter/envAmt", "filter/velAmt", "filter/delay", "filter/attack",
     "filter/decay", "filter/sustain", "filter/release",
     "vca/initLevel", "vca/spread", "vca/volume",
     "vca/envAmt", "vca/velAmt", "vca/delay", "vca/attack",
     "vca/decay", "vca/sustain", "vca/release",
     "lfo/0/freq", "lfo/0/shape", "lfo/0/amt", "lfo/0/dest", "lfo/0/keySync",
     "lfo/1/freq", "lfo/1/shape", "lfo/1/amt", "lfo/1/dest", "lfo/1/keySync",
     "lfo/2/freq", "lfo/2/shape", "lfo/2/amt", "lfo/2/dest", "lfo/2/keySync",
     "lfo/3/freq", "lfo/3/shape", "lfo/3/amt", "lfo/3/dest", "lfo/3/keySync",
     "env3/dest", "env3/amt", "env3/velAmt", "env3/delay", "env3/attack",
     "env3/decay", "env3/sustain", "env3/release",
     "mod/0/source", "mod/0/amt", "mod/0/dest",
     "mod/1/source", "mod/1/amt", "mod/1/dest",
     "mod/2/source", "mod/2/amt", "mod/2/dest",
     "mod/3/source", "mod/3/amt", "mod/3/dest",
     "seq/0/dest", "seq/1/dest", "seq/2/dest", "seq/3/dest",
     "ctrl/0/amt", "ctrl/0/dest", "ctrl/1/amt", "ctrl/1/dest", "ctrl/2/amt",
     "ctrl/2/dest", "ctrl/3/amt", "ctrl/3/dest", "ctrl/4/amt", "ctrl/4/dest",
     "top/bpm", "top/clockDivide", "top/bendRange", "top/seqTrig", "top/unisonMode", "top/unisonAssign",
     "top/arpMode", "env3/repeatOn", "osc/unisonOn", "top/arpOn", "top/gatedSeqOn"
   ] ++ (nil!16) ++ ["splitPoint", \keyboardMode] ++ (4.collect { |n| 16.collect { |i| ("seq/" ++ n.asString ++ "/step/" ++ i.asString).asSymbol } }).flat.collect(_.asString);
   globalParamKey = [
     "program", "bank", "transpose", "fineTune", "channel", "polyChain", "midiClock",
     "localControl", "paramSend", "paramReceive", "programSendReceive", "pressureSendReceive",
     "controllerSendReceive", "sysexSendReceive", "pedalDest", "damperPolarity", "velCurve",
     "pressureCurve", "lcdContrast"
   ];
  }

  *new { |model, bounds = (Rect(0, 0, 1080, 530))|
    ^super.new.init(model).makeWindow(bounds).refreshView;
  }

  init { |argModel|
    model = argModel;
    model.addDependant(this);
  }

  makeWindow { |bounds|
    if (window.notNil) { window.close };
    window = Window("Prophet '08", bounds.resizeBy(60, 0)).front
      .background_(Color.hsv(0.05, 0.9, 0.5));
    view = P08View(window, bounds.moveBy(30, 0));
  }

  refreshView {
    model.ids.do { |param|
      this.update(model, param);
    };
  }

  update { |object, param|
    if (object.class == Prophet08) {
      this.prUpdateView(param);
    };
  }

  prUpdateView { |param|
    var num = param.id;
    var value = param.midiValue;
    var address = case
      { num <= 183 } {
        "layerA/" ++ layerParamKey[num];
      }
      { num <= 199 } {
        "name/" ++ (num - 184);
      }
      { num <= 383 } {
        "layerB/" ++ layerParamKey[num - 200];
      }
      { true } {
        globalParamKey[num - 384];
      };
    this.prSetViewValueByAddress(address, value);
  }

  prSetViewValueByAddress { |address, value|
    var match;
    if ((match = address.findRegexp("^layerA/(.*)")).size > 0) {
      var address = match[1][1];
      if ((match = address.findRegexp("^osc/(.*)")).size > 0) {
        var address = match[1][1];
        view.oscView.perform(address.asSymbol).value_(value);
      };
      if ((match = address.findRegexp("^filter/(.*)")).size > 0) {
        var address = match[1][1];
        view.filtView.perform(address.asSymbol).value_(value);
      };
      if ((match = address.findRegexp("^vca/(.*)")).size > 0) {
        var address = match[1][1];
        view.ampView.perform(address.asSymbol).value_(value);
      };
      if ((match = address.findRegexp("^env3/(.*)")).size > 0) {
        var address = match[1][1];
        view.env3View.perform(address.asSymbol).value_(value);
      };
      if ((match = address.findRegexp("^lfo/(\\d)/(.*)")).size > 0) {
        var address = match[2][1];
        var index = match[1][1].asInt;
        view.lfoViews[index].perform(address.asSymbol).value_(value);
      };
      if ((match = address.findRegexp("^top/(.*)")).size > 0) {
        var address = match[1][1];
        view.topPanel.perform(address.asSymbol).value_(value);
      };
      if ((match = address.findRegexp("^seq/(\\d)/step/(.*)")).size > 0) {
        var index = match[1][1].asInt;
        var address = match[2][1].asInt;
        if (index == 0) {
          view.seqView.steps[address].value_(value);
        };
      };
      if ((match = address.findRegexp("^seq/(\\d)/dest")).size > 0) {
        var index = match[1][1].asInt;
        if (index == 0) {
          view.seqView.dest.value_(value);
        };
      };
      if ((match = address.findRegexp("^mod/(\\d)/(.*)")).size > 0) {
        var address = match[2][1];
        var index = match[1][1].asInt;
        view.modViews[index].perform(address.asSymbol).value_(value);
      };
      if ((match = address.findRegexp("^ctrl/(\\d)/(.*)")).size > 0) {
        var address = match[2][1];
        var index = match[1][1].asInt;
        view.ctrlView.perform((address ++ "s").asSymbol)[index].value_(value);
      };
    };
  }
}
