/*
 * CodePress - Real Time Syntax Highlighting Editor written in JavaScript - http://codepress.org/
 *
 * Copyright (C) 2006 Fernando M.A.d.S. <fermads@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation.
 *
 * Read the full licence: http://www.opensource.org/licenses/lgpl-license.php
 */

// MOD: All local modifications marked with this comment prefix.

// Copied from http://www.google.com/ig/modules/codepress/codepress.js.
// Further modifications to decouple the domain of the script and the
// editor iframe.

CodePress = function(obj) {
  var self = document.getElementById('codepress-iframe');
  self.textarea = obj;
  self.textarea.disabled = true;
  self.textarea.style.overflow = 'hidden';

  // MOD(daniellee): Set height to match existing textarea element
  self.style.height = self.textarea.offsetHeight + 'px';

  // MOD(wwen): change width to 100%
  //self.style.width = self.textarea.clientWidth+'px';
  self.style.width = "100%";

  self.textarea.style.overflow = 'auto';
  // MOD(daniellee): Remove default border. Stylize editor in gge.xml
  //self.style.border = '1px solid gray';
  self.frameBorder = 0; // remove IE internal iframe border
  self.style.visibility = 'hidden';
  self.style.position = 'absolute';
  self.options = self.textarea.className;

  self.initialize = function() {
    self.editor = self.contentWindow.CodePress;
    self.editor.body = self.contentWindow.document.getElementsByTagName('body')[0];
    self.editor.setCode(self.textarea.value);
    self.setOptions();
    self.editor.syntaxHighlight('init');
    self.textarea.style.display = 'none';
    self.style.position = 'static';
    self.style.visibility = 'visible';
    self.style.display = 'inline';
    // MOD(burdon): Call Wrapper.
    if (CodePress.postInitialize) {
      CodePress.postInitialize();
    }
    // MOD(burdon): Spell check off.
    if (self.editor.body.spellcheck) {
      self.editor.body.spellcheck = false;
    }
  }

  self.edit = function(id,language) {
    if(id) self.textarea.value = document.getElementById(id).value;
    if(!self.textarea.disabled) return;
    self.language = language ? language : self.options.replace(/ ?codepress ?| ?readonly-on ?| ?autocomplete-off ?| ?linenumbers-off ?/g,'');
    // if(!CodePress.languages[self.language]) self.language = 'generic';

    var contentDoc = self.document || self.contentDocument || self.contentWindow.documnt;
    contentDoc.open();
    contentDoc.write(codePressEmptyIframeContent);
    contentDoc.close();

    if(self.attachEvent) self.attachEvent('onload',self.initialize);
    else self.addEventListener('load',self.initialize,false);
  }

  self.setOptions = function() {
    if(self.options.match('autocomplete-off')) self.toggleAutoComplete();
    if(self.options.match('readonly-on')) self.toggleReadOnly();
    if(self.options.match('linenumbers-off')) self.toggleLineNumbers();
  }

  self.getCode = function() {
    return self.textarea.disabled ? self.editor.getCode() : self.textarea.value;
  }

  self.setCode = function(code) {
    self.textarea.disabled ? self.editor.setCode(code) : self.textarea.value = code;
  }

  self.toggleAutoComplete = function() {
    self.editor.autocomplete = (self.editor.autocomplete) ? false : true;
  }

  self.toggleReadOnly = function() {
    self.textarea.readOnly = (self.textarea.readOnly) ? false : true;
    if(self.style.display != 'none') // prevent exception on FF + iframe with display:none
      self.editor.readOnly(self.textarea.readOnly ? true : false);
  }

  self.toggleLineNumbers = function() {
    var cn = self.editor.body.className;
    self.editor.body.className = (cn==''||cn=='show-line-numbers') ? 'hide-line-numbers' : 'show-line-numbers';
  }

  self.toggleEditor = function() {
    if(self.textarea.disabled) {
      self.textarea.value = self.getCode();
      self.textarea.disabled = false;
      self.style.display = 'none';
      self.textarea.style.display = 'inline';
    }
    else {
      self.textarea.disabled = true;
      self.setCode(self.textarea.value);
      self.editor.syntaxHighlight('init');
      self.style.display = 'inline';
      self.textarea.style.display = 'none';
    }
  }

  self.edit();
  return self;
}

CodePress.languages = {
  css : 'CSS',
  generic : 'Generic',
  html : 'HTML',
  java : 'Java',
  javascript : 'JavaScript',
  perl : 'Perl',
  ruby : 'Ruby',
  php : 'PHP',
  text : 'Text',
  sql : 'SQL',
  gadget : 'Gadget'
}

// MOD(burdon):
CodePress.getEngine = function() {
  var engine = 'older';
  var ua = navigator.userAgent;
  if(ua.match('MSIE')) engine = 'msie';
  else if(ua.match('KHTML')) engine = 'khtml';
  else if(ua.match('Opera')) engine = 'opera';
  else if(ua.match('Gecko')) engine = 'gecko';
  return engine;
}

CodePress.run = function() {
  CodePress.engine = CodePress.getEngine();
  var s = document.getElementsByTagName('script');
//  for(var i=0,n=s.length;i<n;i++) {
//    if(s[i].src.match(/codepress_wrapper\.js.*/)) {
//      CodePress.path = s[i].src.replace(/codepress_wrapper\.js.*/,'');
//    }
//  }
  var t = document.getElementsByTagName('textarea');
  for(var i=0,n=t.length;i<n;i++) {
    if(t[i].className.match('codepress')) {
      var id = t[i].id;
      t[i].id = id+'_cp';
      window[id] = new CodePress(t[i]);
      t[i].parentNode.insertBefore(window[id], t[i]);
    }
  }
}

// MOD(burdon): Add our language.
CodePress.languages["gm"] = 'GM';

/*
// MOD(burdon): Now triggered by CodePressEditor:init
if(window.attachEvent) window.attachEvent('onload',CodePress.run);
else window.addEventListener('DOMContentLoaded',CodePress.run,false);
*/

var language = "gm";
var engine = "older";
var ua = navigator.userAgent;
var ts = (new Date).getTime(); // timestamp to avoid cache
var lh = location.href;

if(ua.match("MSIE")) engine = "msie";
else if(ua.match("KHTML")) engine = "khtml";
else if(ua.match("Opera")) engine = "opera";
else if(ua.match("Gecko")) engine = "gecko";

if(lh.match("language=")) language = lh.replace(/.*language=(.*?)(&.*)?$/,"$1");

// XSS fix (wwen)
// these are the only languages that GGE supports
if (!language.match(/^(css|gadget|javascript)$/)) {
  language = "gadget";
}

CodePress.path = 'http://www.google.com/ig/modules/codepress/';
var codePressEmptyIframeContent = '<html>\n' +
    '<head>\n' +
    '  <title>CodePress - Real Time Syntax Highlighting Editor written in JavaScript</title>\n' +
    '  <meta name="description" content="CodePress - source code editor window" />\n' +
    '\n' +
    '  <link type="text/css" href="' + CodePress.path + 'codepress.css?ts='+ts+'" rel="stylesheet" />\n' +
    '  <link type="text/css" href="' + CodePress.path + 'languages/'+language+'.css?ts='+ts+'" rel="stylesheet" id="cp-lang-style" />\n' +
    '  <script type="text/javascript" src="' + CodePress.path + 'engines/'+engine+'.js?ts='+ts+'"></script>\n' +
    '  <script type="text/javascript" src="' + CodePress.path + 'languages/'+language+'.js?ts='+ts+'"></script>\n' +
    '\n' +
    '</head>\n' +
    '<body></body>\n' +
    '</html>';
