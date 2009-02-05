var goog = goog || {};
goog.global = this;
goog.evalWorksForGlobals_ = null;
goog.provide = function(name) {
  goog.exportPath_(name)
};
goog.exportPath_ = function(name, opt_object) {
  var parts = name.split("."), cur = goog.global, part;
  while(part = parts.shift()) {
    if(!parts.length && goog.isDef(opt_object)) {
      cur[part] = opt_object
    }else if(cur[part]) {
      cur = cur[part]
    }else {
      cur = (cur[part] = {})
    }
  }
};
goog.getObjectByName = function(name) {
  var parts = name.split("."), cur = goog.global;
  for(var part;part = parts.shift();) {
    if(cur[part]) {
      cur = cur[part]
    }else {
      return null
    }
  }return cur
};
goog.globalize = function(obj, opt_global) {
  var global = opt_global || goog.global;
  for(var x in obj) {
    global[x] = obj[x]
  }
};
goog.addDependency = function(relPath, provides, requires) {
};
goog.require = function(rule) {
};
goog.basePath = "";
goog.nullFunction = function() {
};
goog.JsType_ = {UNDEFINED:"undefined", NUMBER:"number", STRING:"string", BOOLEAN:"boolean", FUNCTION:"function", OBJECT:"object"};
goog.isDef = function(val) {
  return typeof val != goog.JsType_.UNDEFINED
};
goog.isNull = function(val) {
  return val === null
};
goog.isDefAndNotNull = function(val) {
  return goog.isDef(val) && !goog.isNull(val)
};
goog.isArray = function(val) {
  return val instanceof Array || goog.isObject(val) && typeof val.join == goog.JsType_.FUNCTION && typeof val.reverse == goog.JsType_.FUNCTION
};
goog.isArrayLike = function(val) {
  return goog.isObject(val) && typeof val.length == goog.JsType_.NUMBER
};
goog.isDateLike = function(val) {
  return goog.isObject(val) && goog.isFunction(val.getFullYear)
};
goog.isString = function(val) {
  return typeof val == goog.JsType_.STRING
};
goog.isBoolean = function(val) {
  return typeof val == goog.JsType_.BOOLEAN
};
goog.isNumber = function(val) {
  return typeof val == goog.JsType_.NUMBER
};
goog.isFunction = function(val) {
  return typeof val == goog.JsType_.FUNCTION || !(!(val && val.call))
};
goog.isObject = function(val) {
  return val != null && typeof val == goog.JsType_.OBJECT
};
goog.getHashCode = function(obj) {
  if(obj.hasOwnProperty && obj.hasOwnProperty(goog.HASH_CODE_PROPERTY_)) {
    return obj[goog.HASH_CODE_PROPERTY_]
  }if(!obj[goog.HASH_CODE_PROPERTY_]) {
    obj[goog.HASH_CODE_PROPERTY_] = ++goog.hashCodeCounter_
  }return obj[goog.HASH_CODE_PROPERTY_]
};
goog.removeHashCode = function(obj) {
  if("removeAttribute" in obj) {
    obj.removeAttribute(goog.HASH_CODE_PROPERTY_)
  }try {
    delete obj[goog.HASH_CODE_PROPERTY_]
  }catch(ex) {
  }
};
goog.HASH_CODE_PROPERTY_ = "closure_hashCode_";
goog.hashCodeCounter_ = 0;
goog.cloneObject = function(proto) {
  if(goog.isObject(proto)) {
    if(proto.clone) {
      return proto.clone()
    }var clone = goog.isArray(proto) ? [] : {};
    for(var key in proto) {
      clone[key] = goog.cloneObject(proto[key])
    }return clone
  }return proto
};
goog.bind = function(fn, self) {
  var boundArgs = fn.boundArgs_;
  if(arguments.length > 2) {
    var args = Array.prototype.slice.call(arguments, 2);
    if(boundArgs) {
      args.unshift.apply(args, boundArgs)
    }boundArgs = args
  }self = fn.boundSelf_ || self;
  fn = fn.boundFn_ || fn;
  var newfn, context = self || goog.global;
  if(boundArgs) {
    newfn = function() {
      var args = Array.prototype.slice.call(arguments);
      args.unshift.apply(args, boundArgs);
      return fn.apply(context, args)
    }
  }else {
    newfn = function() {
      return fn.apply(context, arguments)
    }
  }newfn.boundArgs_ = boundArgs;
  newfn.boundSelf_ = self;
  newfn.boundFn_ = fn;
  return newfn
};
goog.partial = function(fn) {
  var args = Array.prototype.slice.call(arguments, 1);
  args.unshift(fn, null);
  return goog.bind.apply(null, args)
};
goog.mixin = function(target, source) {
  for(var x in source) {
    target[x] = source[x]
  }
};
goog.now = function() {
  return(new Date).getTime()
};
goog.globalEval = function(script) {
  if(goog.global.execScript) {
    goog.global.execScript(script, "JavaScript")
  }else if(goog.global.eval) {
    if(goog.evalWorksForGlobals_ == null) {
      goog.global.eval("var _et_ = 1;");
      if(typeof goog.global._et_ != "undefined") {
        delete goog.global._et_;
        goog.evalWorksForGlobals_ = true
      }else {
        goog.evalWorksForGlobals_ = false
      }
    }if(goog.evalWorksForGlobals_) {
      goog.global.eval(script)
    }else {
      var doc = goog.global.document, scriptElt = doc.createElement("script");
      scriptElt.type = "text/javascript";
      scriptElt.defer = false;
      scriptElt.text = script;
      doc.body.appendChild(scriptElt);
      doc.body.removeChild(scriptElt)
    }
  }else {
    throw Error("goog.globalEval not available");
  }
};
goog.getMsg = function(str, opt_values) {
  var values = opt_values || {};
  for(var key in values) {
    str = str.replace(new RegExp("\\{\\$" + key + "\\}", "gi"), values[key])
  }return str
};
goog.exportSymbol = function(publicPath, object) {
  goog.exportPath_(publicPath, object)
};
goog.exportProperty = function(object, publicName, symbol) {
  object[publicName] = symbol
};
if(!Function.prototype.apply) {
  Function.prototype.apply = function(oScope, args) {
    var sarg = [], rtrn, call;
    if(!oScope)oScope = goog.global;
    if(!args)args = [];
    for(var i = 0;i < args.length;i++) {
      sarg[i] = "args[" + i + "]"
    }call = "oScope.__applyTemp__.peek().(" + sarg.join(",") + ");";
    if(!oScope.__applyTemp__) {
      oScope.__applyTemp__ = []
    }oScope.__applyTemp__.push(this);
    rtrn = eval(call);
    oScope.__applyTemp__.pop();
    return rtrn
  }
}Function.prototype.bind = function(self) {
  if(arguments.length > 1) {
    var args = Array.prototype.slice.call(arguments, 1);
    args.unshift(this, self);
    return goog.bind.apply(null, args)
  }else {
    return goog.bind(this, self)
  }
};
Function.prototype.partial = function() {
  var args = Array.prototype.slice.call(arguments);
  args.unshift(this, null);
  return goog.bind.apply(null, args)
};
Function.prototype.inherits = function(parentCtor) {
  goog.inherits(this, parentCtor)
};
goog.inherits = function(childCtor, parentCtor) {
  function tempCtor() {
  }
  tempCtor.prototype = parentCtor.prototype;
  childCtor.superClass_ = parentCtor.prototype;
  childCtor.prototype = new tempCtor;
  childCtor.prototype.constructor = childCtor
};
Function.prototype.mixin = function(source) {
  goog.mixin(this.prototype, source)
};
if(!Array.prototype.push) {
  Array.prototype.push = function(var_args) {
    for(var i = 0;i < arguments.length;i++) {
      this[this.length] = arguments[i]
    }return this.length
  }
}if(!Array.prototype.pop) {
  Array.prototype.pop = function() {
    var rv;
    if(this.length) {
      rv = this[this.length - 1];
      this.length--
    }return rv
  }
}Array.prototype.peek = function() {
  return this[this.length - 1]
};
if(!Array.prototype.shift) {
  Array.prototype.shift = function() {
    var rv;
    if(this.length) {
      rv = this[0];
      for(var i = 0;i < this.length - 1;i++) {
        this[i] = this[i + 1]
      }this.length--
    }return rv
  }
}if(!Array.prototype.unshift) {
  Array.prototype.unshift = function(var_args) {
    var numArgs = arguments.length;
    for(var i = this.length - 1;i >= 0;i--) {
      this[i + numArgs] = this[i]
    }for(var j = 0;j < numArgs;j++) {
      this[j] = arguments[j]
    }return this.length
  }
};goog.string = {};
goog.string.startsWith = function(str, prefix) {
  return str.indexOf(prefix) == 0
};
goog.string.endsWith = function(str, suffix) {
  var l = str.length - suffix.length;
  return l >= 0 && str.lastIndexOf(suffix, l) == l
};
goog.string.subs = function(str) {
  for(var i = 1;i < arguments.length;i++) {
    str = str.replace(/\%s/, String(arguments[i]))
  }return str
};
goog.string.collapseWhitespace = function(str) {
  return str.replace(/\s+/g, " ").replace(/^\s+|\s+$/g, "")
};
goog.string.isEmpty = function(str) {
  return/^\s*$/.test(str)
};
goog.string.isEmptySafe = function(str) {
  return goog.string.isEmpty(goog.string.makeSafe(str))
};
goog.string.isAlpha = function(str) {
  return!/[^a-zA-Z]/.test(str)
};
goog.string.isNumeric = function(str) {
  return!/[^0-9]/.test(str)
};
goog.string.isAlphaNumeric = function(str) {
  return!/[^a-zA-Z0-9]/.test(str)
};
goog.string.isSpace = function(ch) {
  return ch == " "
};
goog.string.stripNewlines = function(str) {
  return str.replace(/(\r\n|\r|\n)+/g, " ")
};
goog.string.canonicalizeNewlines = function(str) {
  return str.replace(/(\r\n|\r|\n)/g, "\n")
};
goog.string.normalizeWhitespace = function(str) {
  return str.replace(/\xa0|\s/g, " ")
};
goog.string.normalizeSpaces = function(str) {
  return str.replace(/\xa0|[ \t]+/g, " ")
};
goog.string.trim = function(str) {
  return str.replace(/^\s+|\s+$/g, "")
};
goog.string.trimLeft = function(str) {
  return str.replace(/^\s+/, "")
};
goog.string.trimRight = function(str) {
  return str.replace(/\s+$/, "")
};
goog.string.caseInsensitiveCompare = function(str1, str2) {
  var test1 = String(str1).toLowerCase(), test2 = String(str2).toLowerCase();
  if(test1 < test2) {
    return-1
  }else if(test1 == test2) {
    return 0
  }else {
    return 1
  }
};
goog.string.numerateCompareRegExp_ = /(\.\d+)|(\d+)|(\D+)/g;
goog.string.numerateCompare = function(str1, str2) {
  if(str1 == str2) {
    return 0
  }if(!str1) {
    return-1
  }if(!str2) {
    return 1
  }var tokens1 = str1.toLowerCase().match(goog.string.numerateCompareRegExp_), tokens2 = str2.toLowerCase().match(goog.string.numerateCompareRegExp_), count = Math.min(tokens1.length, tokens2.length);
  for(var i = 0;i < count;i++) {
    var a = tokens1[i], b = tokens2[i];
    if(a != b) {
      var num1 = parseInt(a, 10);
      if(!isNaN(num1)) {
        var num2 = parseInt(b, 10);
        if(!isNaN(num2) && num1 - num2) {
          return num1 - num2
        }
      }return a < b ? -1 : 1
    }
  }if(tokens1.length != tokens2.length) {
    return tokens1.length - tokens2.length
  }return str1 < str2 ? -1 : 1
};
goog.string.encodeUriRegExp_ = /^[a-zA-Z0-9\-_.!~*'()]*$/;
goog.string.urlEncode = function(str) {
  str = String(str);
  if(!goog.string.encodeUriRegExp_.test(str)) {
    return encodeURIComponent(str)
  }return str
};
goog.string.urlDecode = function(str) {
  return decodeURIComponent(str.replace(/\+/g, " "))
};
goog.string.newLineToBr = function(str, opt_xml) {
  return str.replace(/(\r\n|\r|\n)/g, opt_xml ? "<br />" : "<br>")
};
goog.string.htmlEscape = function(str, opt_isLikelyToContainHtmlChars) {
  if(opt_isLikelyToContainHtmlChars) {
    return str.replace(goog.string.amperRe_, goog.string.amperStrRepl_).replace(goog.string.ltRe_, goog.string.ltStrRepl_).replace(goog.string.gtRe_, goog.string.gtStrRepl_).replace(goog.string.quotRe_, goog.string.quotStrRepl_)
  }else {
    if(!goog.string.allRe_.test(str))return str;
    if(str.indexOf(goog.string.amperStrOrig_) != -1) {
      str = str.replace(goog.string.amperRe_, goog.string.amperStrRepl_)
    }if(str.indexOf(goog.string.ltStrOrig_) != -1) {
      str = str.replace(goog.string.ltRe_, goog.string.ltStrRepl_)
    }if(str.indexOf(goog.string.gtStrOrig_) != -1) {
      str = str.replace(goog.string.gtRe_, goog.string.gtStrRepl_)
    }if(str.indexOf(goog.string.quotStrOrig_) != -1) {
      str = str.replace(goog.string.quotRe_, goog.string.quotStrRepl_)
    }return str
  }
};
goog.string.amperStrOrig_ = "&";
goog.string.ltStrOrig_ = "<";
goog.string.gtStrOrig_ = ">";
goog.string.quotStrOrig_ = '"';
goog.string.amperStrRepl_ = "&amp;";
goog.string.ltStrRepl_ = "&lt;";
goog.string.gtStrRepl_ = "&gt;";
goog.string.quotStrRepl_ = "&quot;";
goog.string.amperRe_ = /&/g;
goog.string.ltRe_ = /</g;
goog.string.gtRe_ = />/g;
goog.string.quotRe_ = /\"/g;
goog.string.allRe_ = /[&<>\"]/;
goog.string.unescapeEntities = function(str) {
  if(goog.string.contains(str, "&")) {
    if("document" in goog.global && !goog.string.contains(str, "<")) {
      var el = goog.global.document.createElement("a");
      el.innerHTML = str;
      if(el[goog.string.NORMALIZE_FN_]) {
        el[goog.string.NORMALIZE_FN_]()
      }str = el.firstChild.nodeValue;
      el.innerHTML = ""
    }else {
      return str.replace(/&([^;]+);/g, function(s, entity) {
        switch(entity) {
          case "amp":return"&";
          case "lt":return"<";
          case "gt":return">";
          case "quot":return'"';
          default:if(entity.charAt(0) == "#") {
            var n = Number("0" + entity.substr(1));
            if(!isNaN(n)) {
              return String.fromCharCode(n)
            }
          }return s
        }
      })
    }
  }return str
};
goog.string.NORMALIZE_FN_ = "normalize";
goog.string.whitespaceEscape = function(str, opt_xml) {
  return goog.string.newLineToBr(str.replace(/  /g, " &#160;"), opt_xml)
};
goog.string.stripQuotes = function(str, quotechar) {
  if(str.charAt(0) == quotechar && str.charAt(str.length - 1) == quotechar) {
    return str.substring(1, str.length - 1)
  }return str
};
goog.string.truncate = function(str, chars, opt_protectEscapedCharacters) {
  if(opt_protectEscapedCharacters) {
    str = goog.string.unescapeEntities(str)
  }if(str.length > chars) {
    str = str.substring(0, chars - 3) + "..."
  }if(opt_protectEscapedCharacters) {
    str = goog.string.htmlEscape(str)
  }return str
};
goog.string.truncateMiddle = function(str, chars, opt_protectEscapedCharacters) {
  if(opt_protectEscapedCharacters) {
    str = goog.string.unescapeEntities(str)
  }if(str.length > chars) {
    var half = Math.floor(chars / 2), endPos = str.length - half;
    half += chars % 2;
    str = str.substring(0, half) + "..." + str.substring(endPos)
  }if(opt_protectEscapedCharacters) {
    str = goog.string.htmlEscape(str)
  }return str
};
goog.string.jsEscapeCache_ = {"\u0008":"\\b", "\u000c":"\\f", "\n":"\\n", "\r":"\\r", "\t":"\\t", "\u000b":"\\x0B", '"':'\\"', "'":"\\'", "\\":"\\\\"};
goog.string.quote = function(s) {
  s = String(s);
  if(s.quote) {
    return s.quote()
  }else {
    var rv = '"';
    for(var i = 0;i < s.length;i++) {
      rv += goog.string.escapeChar(s.charAt(i))
    }return rv + '"'
  }
};
goog.string.escapeChar = function(c) {
  if(c in goog.string.jsEscapeCache_) {
    return goog.string.jsEscapeCache_[c]
  }var rv = c, cc = c.charCodeAt(0);
  if(cc > 31 && cc < 127) {
    rv = c
  }else {
    if(cc < 256) {
      rv = "\\x";
      if(cc < 16 || cc > 256) {
        rv += "0"
      }
    }else {
      rv = "\\u";
      if(cc < 4096) {
        rv += "0"
      }
    }rv += cc.toString(16).toUpperCase()
  }return goog.string.jsEscapeCache_[c] = rv
};
goog.string.toMap = function(s) {
  var rv = {};
  for(var i = 0;i < s.length;i++) {
    rv[s.charAt(i)] = true
  }return rv
};
goog.string.JS_REG_EXP_ESCAPE_CHAR_MAP_ = goog.string.toMap("()[]{}+-?*.$^|,:#<!\\");
goog.string.contains = function(s, ss) {
  return s.indexOf(ss) != -1
};
goog.string.regExpEscape = function(s) {
  s = String(s);
  var rv = "", c;
  for(var i = 0;i < s.length;i++) {
    c = s.charAt(i);
    if(c == "\u0008") {
      c = "\\x08"
    }else if(c in goog.string.JS_REG_EXP_ESCAPE_CHAR_MAP_) {
      c = "\\" + c
    }rv += c
  }return rv
};
goog.string.repeat = function(string, length) {
  return(new Array(length + 1)).join(string)
};
goog.string.padNumber = function(num, length, opt_precision) {
  var i = Math.floor(num), s = String(i);
  return goog.string.repeat("0", Math.max(0, length - s.length)) + (goog.isDef(opt_precision) ? num.toFixed(opt_precision) : num)
};
goog.string.makeSafe = function(obj) {
  return obj == null ? "" : String(obj)
};
goog.string.buildString = function() {
  return Array.prototype.join.call(arguments, "")
};
goog.string.getRandomString = function() {
  return Math.floor(Math.random() * 2147483648).toString(36) + (Math.floor(Math.random() * 2147483648) ^ (new Date).getTime()).toString(36)
};goog.array = {};
goog.array.peek = function(array) {
  return array[array.length - 1]
};
goog.array.indexOf = function(arr, obj, opt_fromIndex) {
  if(arr.indexOf) {
    return arr.indexOf(obj, opt_fromIndex)
  }if(Array.indexOf) {
    return Array.indexOf(arr, obj, opt_fromIndex)
  }if(opt_fromIndex == null) {
    opt_fromIndex = 0
  }else if(opt_fromIndex < 0) {
    opt_fromIndex = Math.max(0, arr.length + opt_fromIndex)
  }for(var i = opt_fromIndex;i < arr.length;i++) {
    if(arr[i] === obj)return i
  }return-1
};
goog.array.lastIndexOf = function(arr, obj, opt_fromIndex) {
  if(opt_fromIndex == null) {
    opt_fromIndex = arr.length - 1
  }if(arr.lastIndexOf) {
    return arr.lastIndexOf(obj, opt_fromIndex)
  }if(Array.lastIndexOf) {
    return Array.lastIndexOf(arr, obj, opt_fromIndex)
  }if(opt_fromIndex < 0) {
    opt_fromIndex = Math.max(0, arr.length + opt_fromIndex)
  }for(var i = opt_fromIndex;i >= 0;i--) {
    if(arr[i] === obj)return i
  }return-1
};
goog.array.forEach = function(arr, f, opt_obj) {
  if(arr.forEach) {
    arr.forEach(f, opt_obj)
  }else if(Array.forEach) {
    Array.forEach(arr, f, opt_obj)
  }else {
    var l = arr.length, arr2 = goog.isString(arr) ? arr.split("") : arr;
    for(var i = 0;i < l;i++) {
      f.call(opt_obj, arr2[i], i, arr)
    }
  }
};
goog.array.forEachRight = function(arr, f, opt_obj) {
  var l = arr.length, arr2 = goog.isString(arr) ? arr.split("") : arr;
  for(var i = l - 1;i >= 0;--i) {
    f.call(opt_obj, arr2[i], i, arr)
  }
};
goog.array.filter = function(arr, f, opt_obj) {
  if(arr.filter) {
    return arr.filter(f, opt_obj)
  }if(Array.filter) {
    return Array.filter(arr, f, opt_obj)
  }var l = arr.length, res = [], arr2 = goog.isString(arr) ? arr.split("") : arr;
  for(var i = 0;i < l;i++) {
    if(f.call(opt_obj, arr2[i], i, arr)) {
      res.push(arr2[i])
    }
  }return res
};
goog.array.map = function(arr, f, opt_obj) {
  if(arr.map) {
    return arr.map(f, opt_obj)
  }if(Array.map) {
    return Array.map(arr, f, opt_obj)
  }var l = arr.length, res = [], arr2 = goog.isString(arr) ? arr.split("") : arr;
  for(var i = 0;i < l;i++) {
    res.push(f.call(opt_obj, arr2[i], i, arr))
  }return res
};
goog.array.reduce = function(arr, f, val, opt_obj) {
  var rval = val;
  if(arr.reduce) {
    if(opt_obj) {
      return arr.reduce(goog.bind(f, opt_obj), val)
    }else {
      return arr.reduce(f, val)
    }
  }goog.array.forEach(arr, function(val) {
    rval = f.call(opt_obj, rval, val)
  });
  return rval
};
goog.array.reduceRight = function(arr, f, val, opt_obj) {
  var rval = val;
  if(arr.reduceRight) {
    if(opt_obj) {
      return arr.reduceRight(goog.bind(f, opt_obj), val)
    }else {
      return arr.reduceRight(f, val)
    }
  }goog.array.forEachRight(arr, function(val) {
    rval = f.call(opt_obj, rval, val)
  });
  return rval
};
goog.array.some = function(arr, f, opt_obj) {
  if(arr.some) {
    return arr.some(f, opt_obj)
  }if(Array.some) {
    return Array.some(arr, f, opt_obj)
  }var l = arr.length, arr2 = goog.isString(arr) ? arr.split("") : arr;
  for(var i = 0;i < l;i++) {
    if(f.call(opt_obj, arr2[i], i, arr)) {
      return true
    }
  }return false
};
goog.array.every = function(arr, f, opt_obj) {
  if(arr.every) {
    return arr.every(f, opt_obj)
  }if(Array.every) {
    return Array.every(arr, f, opt_obj)
  }var l = arr.length, arr2 = goog.isString(arr) ? arr.split("") : arr;
  for(var i = 0;i < l;i++) {
    if(!f.call(opt_obj, arr2[i], i, arr)) {
      return false
    }
  }return true
};
goog.array.contains = function(arr, obj) {
  if(arr.contains) {
    return arr.contains(obj)
  }return goog.array.indexOf(arr, obj) > -1
};
goog.array.isEmpty = function(arr) {
  return arr.length == 0
};
goog.array.clear = function(arr) {
  if(!goog.isArray(arr)) {
    for(var i = arr.length - 1;i >= 0;i--) {
      delete arr[i]
    }
  }arr.length = 0
};
goog.array.insert = function(arr, obj) {
  if(!goog.array.contains(arr, obj)) {
    arr.push(obj)
  }
};
goog.array.insertAt = function(arr, obj, opt_i) {
  goog.array.splice(arr, opt_i, 0, obj)
};
goog.array.insertBefore = function(arr, obj, opt_obj2) {
  var i;
  if(arguments.length == 2 || (i = goog.array.indexOf(arr, opt_obj2)) == -1) {
    arr.push(obj)
  }else {
    goog.array.insertAt(arr, obj, i)
  }
};
goog.array.remove = function(arr, obj) {
  var i = goog.array.indexOf(arr, obj), rv;
  if(rv = i != -1) {
    goog.array.removeAt(arr, i)
  }return rv
};
goog.array.removeAt = function(arr, i) {
  return Array.prototype.splice.call(arr, i, 1).length == 1
};
goog.array.clone = function(arr) {
  if(goog.isArray(arr)) {
    return arr.concat()
  }else {
    var rv = [];
    for(var i = 0, len = arr.length;i < len;i++) {
      rv[i] = arr[i]
    }return rv
  }
};
goog.array.toArray = function(object) {
  if(goog.isArray(object)) {
    return object.concat()
  }if(goog.isArrayLike(object)) {
    return goog.array.clone(object)
  }return[object]
};
goog.array.extend = function(arr1, var_args) {
  for(var i = 1;i < arguments.length;i++) {
    var arr2 = arguments[i];
    if(!goog.isArray(arr2)) {
      arr1.push(arr2)
    }else {
      arr1.push.apply(arr1, arr2)
    }
  }
};
goog.array.splice = function(arr, index, howMany, opt_el) {
  return Array.prototype.splice.apply(arr, goog.array.slice(arguments, 1))
};
goog.array.slice = function(arr, start, opt_end) {
  if(arguments.length <= 2) {
    return Array.prototype.slice.call(arr, start)
  }else {
    return Array.prototype.slice.call(arr, start, opt_end)
  }
};
goog.array.find = goog.array.indexOf;
goog.array.insertValue = goog.array.insert;
goog.array.deleteValue = goog.array.remove;
goog.array.removeDuplicates = function(arr, opt_rv) {
  var rv = opt_rv || arr, seen = {}, cursorInsert = 0, cursorRead = 0;
  while(cursorRead < arr.length) {
    var current = arr[cursorRead++], hc = goog.isObject(current) ? goog.getHashCode(current) : current;
    if(!(hc in seen)) {
      seen[hc] = true;
      rv[cursorInsert++] = current
    }
  }rv.length = cursorInsert
};
goog.array.binarySearch = function(arr, target, opt_compareFn) {
  var left = 0, right = arr.length - 1, compareFn = opt_compareFn || goog.array.defaultCompare;
  while(left <= right) {
    var mid = left + right >> 1, compareResult = compareFn(target, arr[mid]);
    if(compareResult > 0) {
      left = mid + 1
    }else if(compareResult < 0) {
      right = mid - 1
    }else {
      return mid
    }
  }return-(left + 1)
};
goog.array.sort = function(arr, opt_compareFn) {
  Array.prototype.sort.call(arr, opt_compareFn || goog.array.defaultCompare)
};
goog.array.defaultCompare = function(a, b) {
  return a > b ? 1 : (a < b ? -1 : 0)
};
goog.array.binaryInsert = function(array, value, opt_compareFn) {
  var index = goog.array.binarySearch(array, value, opt_compareFn);
  if(index < 0) {
    goog.array.insertAt(array, value, -(index + 1));
    return true
  }return false
};
goog.array.binaryRemove = function(array, value, opt_compareFn) {
  var index = goog.array.binarySearch(array, value, opt_compareFn);
  return index >= 0 ? goog.array.removeAt(array, index) : false
};goog.object = {};
goog.object.forEach = function(obj, f, opt_obj) {
  for(var key in obj) {
    f.call(opt_obj, obj[key], key, obj)
  }
};
goog.object.filter = function(obj, f, opt_obj) {
  var res = {};
  for(var key in obj) {
    if(f.call(opt_obj, obj[key], key, obj)) {
      res[key] = obj[key]
    }
  }return res
};
goog.object.map = function(obj, f, opt_obj) {
  var res = {};
  for(var key in obj) {
    res[key] = f.call(opt_obj, obj[key], key, obj)
  }return res
};
goog.object.some = function(obj, f, opt_obj) {
  for(var key in obj) {
    if(f.call(opt_obj, obj[key], key, obj)) {
      return true
    }
  }return false
};
goog.object.every = function(obj, f, opt_obj) {
  for(var key in obj) {
    if(!f.call(opt_obj, obj[key], key, obj)) {
      return false
    }
  }return true
};
goog.object.getCount = function(obj) {
  var rv = 0;
  for(var key in obj) {
    rv++
  }return rv
};
goog.object.contains = function(obj, val) {
  return goog.object.containsValue(obj, val)
};
goog.object.getValues = function(obj) {
  var res = [];
  for(var key in obj) {
    res.push(obj[key])
  }return res
};
goog.object.getKeys = function(obj) {
  var res = [];
  for(var key in obj) {
    res.push(key)
  }return res
};
goog.object.containsKey = function(obj, key) {
  return key in obj
};
goog.object.containsValue = function(obj, val) {
  for(var key in obj) {
    if(obj[key] == val) {
      return true
    }
  }return false
};
goog.object.isEmpty = function(obj) {
  for(var key in obj) {
    return false
  }return true
};
goog.object.clear = function(obj) {
  var keys = goog.object.getKeys(obj);
  for(var i = keys.length - 1;i >= 0;i--) {
    goog.object.remove(obj, keys[i])
  }
};
goog.object.remove = function(obj, key) {
  var rv;
  if(rv = key in obj) {
    delete obj[key]
  }return rv
};
goog.object.add = function(obj, key, val) {
  if(key in obj) {
    throw Error('The object already contains the key "' + key + '"');
  }goog.object.set(obj, key, val)
};
goog.object.get = function(obj, key, opt_val) {
  if(key in obj) {
    return obj[key]
  }return opt_val
};
goog.object.set = function(obj, key, value) {
  obj[key] = value
};
goog.object.clone = function(obj) {
  var res = {};
  for(var key in obj) {
    res[key] = obj[key]
  }return res
};
goog.object.transpose = function(obj) {
  var transposed = {}, keys = goog.object.getKeys(obj);
  for(var i = 0, len = keys.length;i < len;i++) {
    var key = keys[i];
    transposed[obj[key]] = key
  }return transposed
};
goog.object.PROTOTYPE_FIELDS_ = ["constructor", "hasOwnProperty", "isPrototypeOf", "propertyIsEnumerable", "toLocaleString", "toString", "valueOf"];
goog.object.extend = function(target, var_args) {
  var key, source;
  for(var i = 1;i < arguments.length;i++) {
    source = arguments[i];
    for(key in source) {
      target[key] = source[key]
    }for(var j = 0;j < goog.object.PROTOTYPE_FIELDS_.length;j++) {
      key = goog.object.PROTOTYPE_FIELDS_[j];
      if(Object.prototype.hasOwnProperty.call(source, key)) {
        target[key] = source[key]
      }
    }
  }
};goog.structs = {};
goog.structs.getCount = function(col) {
  if(typeof col.getCount == "function") {
    return col.getCount()
  }if(goog.isArrayLike(col) || goog.isString(col)) {
    return col.length
  }return goog.object.getCount(col)
};
goog.structs.getValues = function(col) {
  if(typeof col.getValues == "function") {
    return col.getValues()
  }if(goog.isString(col)) {
    return col.split("")
  }if(goog.isArrayLike(col)) {
    var rv = [], l = col.length;
    for(var i = 0;i < l;i++) {
      rv.push(col[i])
    }return rv
  }return goog.object.getValues(col)
};
goog.structs.getKeys = function(col) {
  if(typeof col.getKeys == "function") {
    return col.getKeys()
  }if(typeof col.getValues == "function") {
    return undefined
  }if(goog.isArrayLike(col) || goog.isString(col)) {
    var rv = [], l = col.length;
    for(var i = 0;i < l;i++) {
      rv.push(i)
    }return rv
  }return goog.object.getKeys(col)
};
goog.structs.contains = function(col, val) {
  if(typeof col.contains == "function") {
    return col.contains(val)
  }if(goog.isArrayLike(col) || goog.isString(col)) {
    return goog.array.contains(col, val)
  }return goog.object.containsValue(col, val)
};
goog.structs.isEmpty = function(col) {
  if(typeof col.isEmpty == "function") {
    return col.isEmpty()
  }if(goog.isArrayLike(col) || goog.isString(col)) {
    return goog.array.isEmpty(col)
  }return goog.object.isEmpty(col)
};
goog.structs.clear = function(col) {
  if(typeof col.clear == "function") {
    col.clear()
  }else if(goog.isArrayLike(col)) {
    goog.array.clear(col)
  }else {
    goog.object.clear(col)
  }
};
goog.structs.forEach = function(col, f, opt_obj) {
  if(typeof col.forEach == "function") {
    col.forEach(f, opt_obj)
  }else if(goog.isArrayLike(col) || goog.isString(col)) {
    goog.array.forEach(col, f, opt_obj)
  }else {
    var keys = goog.structs.getKeys(col), values = goog.structs.getValues(col), l = values.length;
    for(var i = 0;i < l;i++) {
      f.call(opt_obj, values[i], keys && keys[i], col)
    }
  }
};
goog.structs.filter = function(col, f, opt_obj, opt_constr) {
  if(typeof col.filter == "function") {
    return col.filter(f, opt_obj)
  }if(goog.isArrayLike(col) || goog.isString(col)) {
    return goog.array.filter(col, f, opt_obj)
  }var rv, keys = goog.structs.getKeys(col), values = goog.structs.getValues(col), l = values.length;
  if(keys && goog.structs.Map) {
    rv = new (opt_constr || Object);
    for(var i = 0;i < l;i++) {
      if(f.call(opt_obj, values[i], keys[i], col)) {
        goog.structs.Map.set(rv, keys[i], values[i])
      }
    }
  }else if(goog.structs.Set) {
    rv = new (opt_constr || Array);
    for(var i = 0;i < l;i++) {
      if(f.call(opt_obj, values[i], undefined, col)) {
        goog.structs.Set.add(rv, values[i])
      }
    }
  }return rv
};
goog.structs.map = function(col, f, opt_obj, opt_constr) {
  if(typeof col.map == "function") {
    return col.map(f, opt_obj)
  }if(goog.isArrayLike(col) || goog.isString(col)) {
    return goog.array.map(col, f, opt_obj)
  }var rv, keys = goog.structs.getKeys(col), values = goog.structs.getValues(col), l = values.length;
  if(keys && goog.structs.Map) {
    rv = new (opt_constr || Object);
    for(var i = 0;i < l;i++) {
      goog.structs.Map.set(rv, keys[i], f.call(opt_obj, values[i], keys[i], col))
    }
  }else if(goog.structs.Set) {
    rv = new (opt_constr || Array);
    for(var i = 0;i < l;i++) {
      goog.structs.Set.add(rv, keys[i], f.call(opt_obj, values[i], undefined, col))
    }
  }return rv
};
goog.structs.some = function(col, f, opt_obj) {
  if(typeof col.some == "function") {
    return col.some(f, opt_obj)
  }if(goog.isArrayLike(col) || goog.isString(col)) {
    return goog.array.some(col, f, opt_obj)
  }var keys = goog.structs.getKeys(col), values = goog.structs.getValues(col), l = values.length;
  for(var i = 0;i < l;i++) {
    if(f.call(opt_obj, values[i], keys && keys[i], col)) {
      return true
    }
  }return false
};
goog.structs.every = function(col, f, opt_obj) {
  if(typeof col.every == "function") {
    return col.every(f, opt_obj)
  }if(goog.isArrayLike(col) || goog.isString(col)) {
    return goog.array.every(col, f, opt_obj)
  }var keys = goog.structs.getKeys(col), values = goog.structs.getValues(col), l = values.length;
  for(var i = 0;i < l;i++) {
    if(!f.call(opt_obj, values[i], keys && keys[i], col)) {
      return false
    }
  }return true
};goog.structs.Map = function(opt_map) {
  this.map_ = {};
  this.keys_ = [];
  if(opt_map) {
    this.addAll(opt_map)
  }
};
goog.structs.Map.keyPrefix_ = ":";
goog.structs.Map.keyPrefixCharCode_ = goog.structs.Map.keyPrefix_.charCodeAt(0);
goog.structs.Map.prototype.count_ = 0;
goog.structs.Map.toInternalKey_ = function(key) {
  key = String(key);
  if(key in Object.prototype) {
    return goog.structs.Map.keyPrefix_ + key
  }else if(key.charCodeAt(0) == goog.structs.Map.keyPrefixCharCode_) {
    return goog.structs.Map.keyPrefix_ + key
  }else {
    return key
  }
};
goog.structs.Map.fromInternalKey_ = function(internalKey) {
  if(internalKey.charCodeAt(0) == goog.structs.Map.keyPrefixCharCode_) {
    return internalKey.substring(1)
  }else {
    return internalKey
  }
};
goog.structs.Map.prototype.getCount = function() {
  return this.count_
};
goog.structs.Map.prototype.getValues = function() {
  this.cleanupKeysArray_();
  var rv = [];
  for(var i = 0;i < this.keys_.length;i++) {
    var key = this.keys_[i];
    rv.push(this.map_[key])
  }return rv
};
goog.structs.Map.prototype.getKeys = function() {
  this.cleanupKeysArray_();
  var rv = [];
  for(var i = 0;i < this.keys_.length;i++) {
    var key = this.keys_[i];
    rv.push(goog.structs.Map.fromInternalKey_(key))
  }return rv
};
goog.structs.Map.prototype.containsKey = function(key) {
  return goog.structs.Map.toInternalKey_(key) in this.map_
};
goog.structs.Map.prototype.containsValue = function(val) {
  for(var i = 0;i < this.keys_.length;i++) {
    var key = this.keys_[i];
    if(key in this.map_) {
      if(this.map_[key] == val) {
        return true
      }
    }
  }return false
};
goog.structs.Map.prototype.isEmpty = function() {
  return this.count_ == 0
};
goog.structs.Map.prototype.clear = function() {
  this.map_ = {};
  this.keys_.length = 0;
  this.count_ = 0
};
goog.structs.Map.prototype.remove = function(key) {
  var internalKey = goog.structs.Map.toInternalKey_(key);
  if(goog.object.remove(this.map_, internalKey)) {
    this.count_--;
    if(this.keys_.length > 2 * this.count_) {
      this.cleanupKeysArray_()
    }return true
  }return false
};
goog.structs.Map.prototype.cleanupKeysArray_ = function() {
  if(this.count_ != this.keys_.length) {
    var srcIndex = 0, destIndex = 0;
    while(srcIndex < this.keys_.length) {
      var key = this.keys_[srcIndex];
      if(key in this.map_) {
        this.keys_[destIndex++] = key
      }srcIndex++
    }this.keys_.length = destIndex
  }if(this.count_ != this.keys_.length) {
    var seen = {}, srcIndex = 0, destIndex = 0;
    while(srcIndex < this.keys_.length) {
      var key = this.keys_[srcIndex];
      if(!(key in seen)) {
        this.keys_[destIndex++] = key;
        seen[key] = 1
      }srcIndex++
    }this.keys_.length = destIndex
  }
};
goog.structs.Map.prototype.get = function(key, opt_val) {
  var internalKey = goog.structs.Map.toInternalKey_(key);
  if(internalKey in this.map_) {
    return this.map_[internalKey]
  }return opt_val
};
goog.structs.Map.prototype.set = function(key, value) {
  var internalKey = goog.structs.Map.toInternalKey_(key);
  if(!(internalKey in this.map_)) {
    this.count_++;
    this.keys_.push(internalKey)
  }this.map_[internalKey] = value
};
goog.structs.Map.prototype.addAll = function(map) {
  var keys, values;
  if(map instanceof goog.structs.Map) {
    keys = map.getKeys();
    values = map.getValues()
  }else {
    keys = goog.object.getKeys(map);
    values = goog.object.getValues(map)
  }for(var i = 0;i < keys.length;i++) {
    this.set(keys[i], values[i])
  }
};
goog.structs.Map.prototype.clone = function() {
  return new goog.structs.Map(this)
};
goog.structs.Map.getCount = function(map) {
  return goog.structs.getCount(map)
};
goog.structs.Map.getValues = function(map) {
  return goog.structs.getValues(map)
};
goog.structs.Map.getKeys = function(map) {
  if(typeof map.getKeys == goog.JsType_.FUNCTION) {
    return map.getKeys()
  }var rv = [];
  if(goog.isArrayLike(map)) {
    for(var i = 0;i < map.length;i++) {
      rv.push(i)
    }
  }else {
    return goog.object.getKeys(map)
  }return rv
};
goog.structs.Map.containsKey = function(map, key) {
  if(typeof map.containsKey == goog.JsType_.FUNCTION) {
    return map.containsKey(key)
  }if(goog.isArrayLike(map)) {
    return key < map.length
  }return goog.object.containsKey(map, key)
};
goog.structs.Map.containsValue = function(map, val) {
  return goog.structs.contains(map, val)
};
goog.structs.Map.isEmpty = function(map) {
  return goog.structs.isEmpty(map)
};
goog.structs.Map.clear = function(map) {
  goog.structs.clear(map)
};
goog.structs.Map.remove = function(map, key) {
  if(typeof map.remove == goog.JsType_.FUNCTION) {
    return map.remove(key)
  }if(goog.isArrayLike(map)) {
    return goog.array.removeAt(map, key)
  }return goog.object.remove(map, key)
};
goog.structs.Map.add = function(map, key, val) {
  if(typeof map.add == goog.JsType_.FUNCTION) {
    map.add(key, val)
  }else if(goog.structs.Map.containsKey(map, key)) {
    throw Error('The collection already contains the key "' + key + '"');
  }else {
    goog.object.set(map, key, val)
  }
};
goog.structs.Map.get = function(map, key, opt_val) {
  if(typeof map.get == goog.JsType_.FUNCTION) {
    return map.get(key, opt_val)
  }if(goog.structs.Map.containsKey(map, key)) {
    return map[key]
  }return opt_val
};
goog.structs.Map.set = function(map, key, val) {
  if(typeof map.set == goog.JsType_.FUNCTION) {
    map.set(key, val)
  }else {
    map[key] = val
  }
};goog.structs.Set = function(opt_set) {
  this.map_ = new goog.structs.Map;
  if(opt_set) {
    this.addAll(opt_set)
  }
};
goog.structs.Set.getKey_ = function(val) {
  var type = typeof val;
  if(type == "object") {
    return"o" + goog.getHashCode(val)
  }else {
    return type.substr(0, 1) + val
  }
};
goog.structs.Set.prototype.getCount = function() {
  return this.map_.getCount()
};
goog.structs.Set.prototype.add = function(obj) {
  this.map_.set(goog.structs.Set.getKey_(obj), obj)
};
goog.structs.Set.prototype.addAll = function(set) {
  var values = goog.structs.Set.getValues(set), l = values.length;
  for(var i = 0;i < l;i++) {
    this.add(values[i])
  }
};
goog.structs.Set.prototype.remove = function(obj) {
  return this.map_.remove(goog.structs.Set.getKey_(obj))
};
goog.structs.Set.prototype.clear = function() {
  this.map_.clear()
};
goog.structs.Set.prototype.isEmpty = function() {
  return this.map_.isEmpty()
};
goog.structs.Set.prototype.contains = function(obj) {
  return this.map_.containsKey(goog.structs.Set.getKey_(obj))
};
goog.structs.Set.prototype.getValues = function() {
  return this.map_.getValues()
};
goog.structs.Set.prototype.clone = function() {
  return new goog.structs.Set(this)
};
goog.structs.Set.getCount = function(col) {
  return goog.structs.getCount(col)
};
goog.structs.Set.getValues = function(col) {
  return goog.structs.getValues(col)
};
goog.structs.Set.contains = function(col, val) {
  return goog.structs.contains(col, val)
};
goog.structs.Set.isEmpty = function(col) {
  return goog.structs.isEmpty(col)
};
goog.structs.Set.clear = function(col) {
  goog.structs.clear(col)
};
goog.structs.Set.remove = function(col, val) {
  if(typeof col.remove == "function") {
    return col.remove(val)
  }else if(goog.isArrayLike(col)) {
    return goog.array.remove(col, val)
  }else {
    for(var key in col) {
      if(col[key] == val) {
        delete col[key];
        return true
      }
    }return false
  }
};
goog.structs.Set.add = function(col, val) {
  if(typeof col.add == "function") {
    col.add(val)
  }else if(goog.isArrayLike(col)) {
    col[col.length] = val
  }else {
    throw Error('The collection does not know how to add "' + val + '"');
  }
};goog.debug = {};
goog.debug.catchErrors = function(opt_logger, opt_cancel, opt_target) {
  var logger = opt_logger || goog.debug.LogManager.getRoot(), target = opt_target || goog.global, oldErrorHandler = target.onerror;
  target.onerror = function(message, url, line) {
    if(oldErrorHandler) {
      oldErrorHandler(message, url, line)
    }var file = String(url).split(/[\/\\]/).pop();
    logger.severe("Error: " + message + " (" + file + " @ Line: " + line + ")");
    return Boolean(opt_cancel)
  }
};
goog.debug.expose = function(obj) {
  if(!goog.isDef(obj))return"undefined";
  if(obj == null)return"NULL";
  var str = [];
  for(var x in obj) {
    var s = x + " = ";
    try {
      s += obj[x]
    }catch(e) {
      s += "*** " + e + " ***"
    }str.push(s)
  }return str.join("\n")
};
goog.debug.deepExpose = function(obj) {
  var previous = new goog.structs.Set, str = [], helper = function(obj, space) {
    var nestspace = space + "  ", indentMultiline = function(str) {
      return str.replace(/\n/g, "\n" + space)
    };
    try {
      if(!goog.isDef(obj)) {
        str.push("undefined")
      }else if(goog.isNull(obj)) {
        str.push("NULL")
      }else if(goog.isString(obj)) {
        str.push('"' + indentMultiline(obj) + '"')
      }else if(goog.isFunction(obj)) {
        str.push(indentMultiline(String(obj)))
      }else if(goog.isObject(obj)) {
        if(previous.contains(obj)) {
          str.push("*** reference loop detected ***")
        }else {
          previous.add(obj);
          str.push("{");
          for(var x in obj) {
            str.push("\n");
            str.push(nestspace);
            str.push(x + " = ");
            helper(obj[x], nestspace)
          }str.push("\n" + space + "}")
        }
      }else {
        str.push(obj)
      }
    }catch(e) {
      str.push("*** " + e + " ***")
    }
  };
  helper(obj, "");
  return str.join("")
};
goog.debug.exposeArray = function(arr) {
  var str = [];
  for(var i = 0;i < arr.length;i++) {
    if(goog.isArray(arr[i])) {
      str.push(goog.debug.exposeArray(arr[i]))
    }else {
      str.push(arr[i])
    }
  }return"[ " + str.join(", ") + " ]"
};
goog.debug.exposeException = function(err, opt_fn) {
  try {
    var e = goog.debug.normalizeErrorObject(err), error = "Message: " + goog.string.htmlEscape(e.message) + '\nUrl: <a href="view-source:' + e.fileName + '" target="_new">' + e.fileName + "</a>\nLine: " + e.lineNumber + "\n\nBrowser stack:\n" + goog.string.htmlEscape(e.stack + "-> ") + "[end]\n\nJS stack traversal:\n" + goog.string.htmlEscape(goog.debug.getStacktrace(opt_fn) + "-> ");
    return error
  }catch(e2) {
    return"Exception trying to expose exception! You win, we lose. " + e2
  }
};
goog.debug.normalizeErrorObject = function(err) {
  return typeof err == "string" ? {message:err, name:"Unknown error", lineNumber:"Not available", fileName:goog.global.document.location.href, stack:"Not available"} : (!err.lineNumber || !err.fileName || !err.stack ? {message:err.message, name:err.name, lineNumber:"Not available", fileName:goog.global.document.location.href, stack:"Not available"} : err)
};
goog.debug.enhanceError = function(err, opt_message) {
  if(typeof err == "string") {
    err = Error(err)
  }if(!err.stack) {
    err.stack = goog.debug.getStacktrace(arguments.callee.caller)
  }if(opt_message) {
    var x = 0;
    while(err["message" + x]) {
      ++x
    }err["message" + x] = String(opt_message)
  }return err
};
goog.debug.getStacktraceSimple = function(opt_depth) {
  var sb = [], fn = arguments.callee.caller, depth = 0;
  while(fn && (!opt_depth || depth < opt_depth)) {
    sb.push(goog.debug.getFunctionName(fn));
    sb.push("()\n");
    try {
      fn = fn.caller
    }catch(e) {
      sb.push("[exception trying to get caller]\n");
      break
    }depth++;
    if(depth >= goog.debug.MAX_STACK_DEPTH) {
      sb.push("[...long stack...]");
      break
    }
  }if(opt_depth && depth >= opt_depth) {
    sb.push("[...reached max depth limit...]")
  }else {
    sb.push("[end]")
  }return sb.join("")
};
goog.debug.MAX_STACK_DEPTH = 50;
goog.debug.getStacktrace = function(opt_fn) {
  return goog.debug.getStacktraceHelper_(opt_fn || arguments.callee.caller, [])
};
goog.debug.getStacktraceHelper_ = function(fn, visited) {
  var sb = [];
  if(goog.array.contains(visited, fn)) {
    sb.push("[...circular reference...]")
  }else if(fn && visited.length < goog.debug.MAX_STACK_DEPTH) {
    sb.push(goog.debug.getFunctionName(fn) + "(");
    var args = fn.arguments;
    for(var i = 0;i < args.length;i++) {
      if(i > 0) {
        sb.push(", ")
      }var arg = typeof args[i] == "object" && args[i].toSource ? args[i].toSource() : String(args[i]);
      if(arg.length > 40) {
        arg = arg.substr(0, 40) + "..."
      }sb.push(arg)
    }visited.push(fn);
    sb.push(")\n");
    try {
      sb.push(goog.debug.getStacktraceHelper_(fn.caller, visited))
    }catch(e) {
      sb.push("[exception trying to get caller]\n")
    }
  }else if(fn) {
    sb.push("[...long stack...]")
  }else {
    sb.push("[end]")
  }return sb.join("")
};
goog.debug.getFunctionName = function(fn) {
  if(!goog.debug.fnNameCache_[fn]) {
    var matches = /function ([^\(]+)/.exec(String(fn));
    if(matches) {
      var method = matches[1], hasDollarSigns = /^\$(.+)\$$/.exec(method);
      if(hasDollarSigns) {
        method = hasDollarSigns[1].replace(/\${1,2}/g, ".")
      }goog.debug.fnNameCache_[fn] = method
    }else {
      goog.debug.fnNameCache_[fn] = "[Anonymous]"
    }
  }return goog.debug.fnNameCache_[fn]
};
goog.debug.getAnonFunctionName_ = function(fn, opt_obj, opt_prefix, opt_depth) {
  if(document.all) {
    return""
  }var obj = opt_obj || goog.global, prefix = opt_prefix || "", depth = opt_depth || 0;
  if(obj == fn) {
    return prefix
  }for(var i in obj) {
    if(i == "Packages" || i == "sun" || i == "netscape" || i == "java") {
      continue
    }if(obj[i] == fn) {
      return prefix + i
    }if((typeof obj[i] == "function" || typeof obj[i] == "object") && obj[i] != goog.global && obj[i] != goog.global.document && obj.hasOwnProperty(i) && depth < 6) {
      var rv = goog.debug.getAnonFunctionName_(fn, obj[i], prefix + i + ".", depth + 1);
      if(rv)return rv
    }
  }return""
};
goog.debug.fnNameCache_ = {};goog.debug.LogRecord = function(level, msg, loggerName) {
  this.sequenceNumber_ = goog.debug.LogRecord.nextSequenceNumber_++;
  this.time_ = goog.now();
  this.level_ = level;
  this.msg_ = msg;
  this.loggerName_ = loggerName
};
goog.debug.LogRecord.prototype.exception_ = null;
goog.debug.LogRecord.prototype.exceptionText_ = null;
goog.debug.LogRecord.nextSequenceNumber_ = 0;
goog.debug.LogRecord.prototype.getLoggerName = function() {
  return this.loggerName_
};
goog.debug.LogRecord.prototype.getException = function() {
  return this.exception_
};
goog.debug.LogRecord.prototype.setException = function(exception) {
  this.exception_ = exception
};
goog.debug.LogRecord.prototype.getExceptionText = function() {
  return this.exceptionText_
};
goog.debug.LogRecord.prototype.setExceptionText = function(text) {
  this.exceptionText_ = text
};
goog.debug.LogRecord.prototype.getLevel = function() {
  return this.level_
};
goog.debug.LogRecord.prototype.setLevel = function(level) {
  this.level_ = level
};
goog.debug.LogRecord.prototype.getMessage = function() {
  return this.msg_
};
goog.debug.LogRecord.prototype.getMillis = function() {
  return this.time_
};goog.debug.Logger = function(name) {
  this.name_ = name;
  this.parent_ = null;
  this.children_ = {};
  this.handlers_ = []
};
goog.debug.Logger.prototype.level_ = null;
goog.debug.Logger.Level = function(name, value) {
  this.name = name;
  this.value = value
};
goog.debug.Logger.Level.prototype.toString = function() {
  return this.name
};
goog.debug.Logger.Level.OFF = new goog.debug.Logger.Level("OFF", Infinity);
goog.debug.Logger.Level.SHOUT = new goog.debug.Logger.Level("SHOUT", 1200);
goog.debug.Logger.Level.SEVERE = new goog.debug.Logger.Level("SEVERE", 1000);
goog.debug.Logger.Level.WARNING = new goog.debug.Logger.Level("WARNING", 900);
goog.debug.Logger.Level.INFO = new goog.debug.Logger.Level("INFO", 800);
goog.debug.Logger.Level.CONFIG = new goog.debug.Logger.Level("CONFIG", 700);
goog.debug.Logger.Level.FINE = new goog.debug.Logger.Level("FINE", 500);
goog.debug.Logger.Level.FINER = new goog.debug.Logger.Level("FINER", 400);
goog.debug.Logger.Level.FINEST = new goog.debug.Logger.Level("FINEST", 300);
goog.debug.Logger.Level.ALL = new goog.debug.Logger.Level("ALL", 0);
goog.debug.Logger.getLogger = function(name) {
  return goog.debug.LogManager.getLogger(name)
};
goog.debug.Logger.prototype.addHandler = function(handler) {
  this.handlers_.push(handler)
};
goog.debug.Logger.prototype.removeHandler = function(handler) {
  return goog.array.remove(this.handlers_, handler)
};
goog.debug.Logger.prototype.getParent = function() {
  return this.parent_
};
goog.debug.Logger.prototype.setLevel = function(level) {
  this.level_ = level
};
goog.debug.Logger.prototype.getLevel = function() {
  return this.level_
};
goog.debug.Logger.prototype.getEffectiveLevel = function() {
  if(this.level_) {
    return this.level_
  }if(this.parent_) {
    return this.parent_.getEffectiveLevel()
  }return null
};
goog.debug.Logger.prototype.isLoggable = function(level) {
  if(this.level_) {
    return level.value >= this.level_.value
  }if(this.parent_) {
    return this.parent_.isLoggable(level)
  }return false
};
goog.debug.Logger.prototype.log = function(level, msg, opt_exception) {
  if(!this.isLoggable(level)) {
    return
  }var logRecord = new goog.debug.LogRecord(level, String(msg), this.name_);
  if(opt_exception) {
    logRecord.setException(opt_exception);
    logRecord.setExceptionText(goog.debug.exposeException(opt_exception, arguments.callee.caller))
  }this.logRecord(logRecord)
};
goog.debug.Logger.prototype.severe = function(msg, opt_exception) {
  this.log(goog.debug.Logger.Level.SEVERE, msg, opt_exception)
};
goog.debug.Logger.prototype.logRecord = function(logRecord) {
  if(!this.isLoggable(logRecord.getLevel())) {
    return
  }var target = this;
  while(target) {
    target.callPublish_(logRecord);
    target = target.getParent()
  }
};
goog.debug.Logger.prototype.callPublish_ = function(logRecord) {
  for(var i = 0;i < this.handlers_.length;i++) {
    this.handlers_[i](logRecord)
  }
};
goog.debug.Logger.prototype.setParent_ = function(parent) {
  this.parent_ = parent
};
goog.debug.Logger.prototype.addChild_ = function(name, logger) {
  this.children_[name] = logger
};
goog.debug.LogManager = {};
goog.debug.LogManager.loggers_ = {};
goog.debug.LogManager.rootLogger_ = null;
goog.debug.LogManager.initialize = function() {
  if(!goog.debug.LogManager.rootLogger_) {
    goog.debug.LogManager.rootLogger_ = new goog.debug.Logger("");
    goog.debug.LogManager.loggers_[""] = goog.debug.LogManager.rootLogger_;
    goog.debug.LogManager.rootLogger_.setLevel(goog.debug.Logger.Level.CONFIG)
  }
};
goog.debug.LogManager.getLoggers = function() {
  return goog.debug.LogManager.loggers_
};
goog.debug.LogManager.getRoot = function() {
  goog.debug.LogManager.initialize();
  return goog.debug.LogManager.rootLogger_
};
goog.debug.LogManager.getLogger = function(name) {
  goog.debug.LogManager.initialize();
  var logger = goog.debug.LogManager.loggers_[name];
  if(logger == null) {
    logger = goog.debug.LogManager.createLogger_(name)
  }return logger
};
goog.debug.LogManager.createLogger_ = function(name) {
  var logger = new goog.debug.Logger(name), parts = name.split("."), leafName = parts[parts.length - 1];
  parts.length = parts.length - 1;
  var parentName = parts.join("."), parentLogger = goog.debug.LogManager.getLogger(parentName);
  parentLogger.addChild_(leafName, logger);
  logger.setParent_(parentLogger);
  goog.debug.LogManager.loggers_[name] = logger;
  return logger
};goog.debug.Formatter = function(opt_prefix) {
  this.prefix_ = opt_prefix || "";
  this.relativeTimeStart_ = goog.now()
};
goog.debug.Formatter.prototype.showAbsoluteTime = true;
goog.debug.Formatter.prototype.showRelativeTime = true;
goog.debug.Formatter.prototype.showLoggerName = true;
goog.debug.Formatter.prototype.formatRecord = function(logRecord) {
  throw Error("Must override formatRecord");
};
goog.debug.Formatter.getDateTimeStamp_ = function(logRecord) {
  var time = new Date(logRecord.getMillis());
  return goog.debug.Formatter.getTwoDigitString_(time.getFullYear() - 2000) + goog.debug.Formatter.getTwoDigitString_(time.getMonth() + 1) + goog.debug.Formatter.getTwoDigitString_(time.getDate()) + " " + goog.debug.Formatter.getTwoDigitString_(time.getHours()) + ":" + goog.debug.Formatter.getTwoDigitString_(time.getMinutes()) + ":" + goog.debug.Formatter.getTwoDigitString_(time.getSeconds()) + "." + goog.debug.Formatter.getTwoDigitString_(Math.floor(time.getMilliseconds() / 10))
};
goog.debug.Formatter.getTwoDigitString_ = function(n) {
  if(n < 10) {
    return"0" + n
  }return String(n)
};
goog.debug.Formatter.getRelativeTime_ = function(logRecord, relativeTimeStart) {
  var ms = logRecord.getMillis() - relativeTimeStart, sec = ms / 1000, str = sec.toFixed(3), spacesToPrepend = 0;
  if(sec < 1) {
    spacesToPrepend = 2
  }else {
    while(sec < 100) {
      spacesToPrepend++;
      sec *= 10
    }
  }while(spacesToPrepend-- > 0) {
    str = " " + str
  }return str
};
goog.debug.HtmlFormatter = function(opt_prefix) {
  goog.debug.Formatter.call(this, opt_prefix)
};
goog.debug.HtmlFormatter.inherits(goog.debug.Formatter);
goog.debug.HtmlFormatter.prototype.formatRecord = function(logRecord) {
  var className;
  switch(logRecord.getLevel()) {
    case goog.debug.Logger.Level.SHOUT:className = "dbg-sh";
    break;
    case goog.debug.Logger.Level.SEVERE:className = "dbg-sev";
    break;
    case goog.debug.Logger.Level.WARNING:className = "dbg-w";
    break;
    case goog.debug.Logger.Level.INFO:className = "dbg-i";
    break;
    case goog.debug.Logger.Level.FINE:default:className = "dbg-f";
    break
  }
  var sb = [];
  sb.push(this.prefix_);
  sb.push(" ");
  if(this.showAbsoluteTime) {
    sb.push("[" + goog.debug.Formatter.getDateTimeStamp_(logRecord) + "] ")
  }if(this.showRelativeTime) {
    sb.push("[");
    sb.push(goog.string.whitespaceEscape(goog.debug.Formatter.getRelativeTime_(logRecord, this.relativeTimeStart_)));
    sb.push("s] ")
  }if(this.showLoggerName) {
    sb.push("[");
    sb.push(goog.string.htmlEscape(logRecord.getLoggerName()));
    sb.push("] ")
  }sb.push('<span class="' + className + '">');
  sb.push(goog.string.newLineToBr(goog.string.whitespaceEscape(goog.string.htmlEscape(logRecord.getMessage()))));
  if(logRecord.getException()) {
    sb.push("<br>");
    sb.push(goog.string.newLineToBr(goog.string.whitespaceEscape(logRecord.getExceptionText())))
  }sb.push("</span><br>");
  return sb.join("")
};
goog.debug.TextFormatter = function(opt_prefix) {
  goog.debug.Formatter.call(this, opt_prefix)
};
goog.debug.TextFormatter.inherits(goog.debug.Formatter);
goog.debug.TextFormatter.prototype.formatRecord = function(logRecord) {
  var sb = [];
  sb.push(this.prefix_);
  sb[sb.length] = " ";
  if(this.showAbsoluteTime) {
    sb.push("[");
    sb.push(goog.debug.Formatter.getDateTimeStamp_(logRecord));
    sb.push("] ")
  }if(this.showRelativeTime) {
    sb.push("[");
    sb.push(goog.debug.Formatter.getRelativeTime_(logRecord, this.relativeTimeStart_));
    sb.push("s] ")
  }if(this.showLoggerName) {
    sb.push("[" + logRecord.getLoggerName() + "] ")
  }sb.push(logRecord.getMessage());
  sb.push("\n");
  return sb.join("")
};goog.structs.CircularBuffer = function(opt_maxSize) {
  this.maxSize_ = opt_maxSize || 100;
  this.buff_ = []
};
goog.structs.CircularBuffer.prototype.nextPtr_ = 0;
goog.structs.CircularBuffer.prototype.add = function(item) {
  this.buff_[this.nextPtr_] = item;
  this.nextPtr_ = (this.nextPtr_ + 1) % this.maxSize_
};
goog.structs.CircularBuffer.prototype.get = function(index) {
  index = this.normalizeIndex_(index);
  return this.buff_[index]
};
goog.structs.CircularBuffer.prototype.set = function(index, item) {
  index = this.normalizeIndex_(index);
  this.buff_[index] = item
};
goog.structs.CircularBuffer.prototype.getCount = function() {
  return this.buff_.length
};
goog.structs.CircularBuffer.prototype.isEmpty = function() {
  return this.buff_.length == 0
};
goog.structs.CircularBuffer.prototype.clear = function() {
  this.buff_.length = 0;
  this.nextPtr_ = 0
};
goog.structs.CircularBuffer.prototype.getValues = function() {
  var l = this.getCount(), rv = new Array(l);
  for(var i = 0;i < l;i++) {
    rv[i] = this.get(i)
  }return rv
};
goog.structs.CircularBuffer.prototype.getKeys = function() {
  var rv = [], l = this.getCount();
  for(var i = 0;i < l;i++) {
    rv[i] = i
  }return rv
};
goog.structs.CircularBuffer.prototype.containsKey = function(key) {
  return key < this.getCount()
};
goog.structs.CircularBuffer.prototype.containsValue = function(value) {
  var l = this.getCount();
  for(var i = 0;i < l;i++) {
    if(this.get(i) == value) {
      return true
    }
  }return false
};
goog.structs.CircularBuffer.prototype.normalizeIndex_ = function(index) {
  if(index >= this.buff_.length) {
    throw Error("Out of bounds exception");
  }if(this.buff_.length < this.maxSize_) {
    return index
  }return(this.nextPtr_ + Number(index)) % this.maxSize_
};goog.debug.DebugWindow = function(opt_identifier, opt_prefix) {
  this.identifier_ = opt_identifier || "";
  this.prefix_ = opt_prefix || "";
  this.outputBuffer_ = [];
  this.savedMessages_ = new goog.structs.CircularBuffer(goog.debug.DebugWindow.MAX_SAVED);
  this.publishHandler_ = goog.bind(this.onPublish_, this);
  this.formatter_ = new goog.debug.HtmlFormatter(this.prefix_);
  this.filteredLoggers_ = {};
  this.setCapturing(true);
  this.enabled_ = this.getCookie_("enabled") == "1";
  goog.global.setInterval(goog.bind(this.saveWindowPositionSize_, this), 7500)
};
goog.debug.DebugWindow.MAX_SAVED = 500;
goog.debug.DebugWindow.COOKIE_TIME = 2592000000;
goog.debug.DebugWindow.prototype.welcomeMessage_ = "LOGGING";
goog.debug.DebugWindow.prototype.win_ = null;
goog.debug.DebugWindow.prototype.winOpening_ = false;
goog.debug.DebugWindow.prototype.isCapturing_ = false;
goog.debug.DebugWindow.showedBlockedAlert_ = false;
goog.debug.DebugWindow.prototype.bufferTimeout_ = null;
goog.debug.DebugWindow.prototype.lastCall_ = goog.now();
goog.debug.DebugWindow.prototype.init = function() {
  if(this.enabled_) {
    this.openWindow_()
  }
};
goog.debug.DebugWindow.prototype.setCapturing = function(capturing) {
  if(capturing == this.isCapturing_) {
    return
  }var rootLogger = goog.debug.LogManager.getRoot();
  if(capturing) {
    rootLogger.addHandler(this.publishHandler_)
  }else {
    rootLogger.removeHandler(this.publishHandler_)
  }
};
goog.debug.DebugWindow.prototype.onPublish_ = function(logRecord) {
  if(this.filteredLoggers_[logRecord.getLoggerName()]) {
    return
  }var html = this.formatter_.formatRecord(logRecord);
  this.write_(html)
};
goog.debug.DebugWindow.prototype.write_ = function(html) {
  if(this.enabled_) {
    this.openWindow_();
    this.savedMessages_.add(html);
    this.writeToLog_(html)
  }else {
    this.savedMessages_.add(html)
  }
};
goog.debug.DebugWindow.prototype.writeToLog_ = function(html) {
  this.outputBuffer_.push(html);
  goog.global.clearTimeout(this.bufferTimeout_);
  if(goog.now() - this.lastCall_ > 750) {
    this.writeBufferToLog_()
  }else {
    this.bufferTimeout_ = goog.global.setTimeout(goog.bind(this.writeBufferToLog_, this), 250)
  }
};
goog.debug.DebugWindow.prototype.writeBufferToLog_ = function() {
  this.lastCall_ = goog.now();
  if(this.win_) {
    var body = this.win_.document.body, scroll = body && body.scrollHeight - (body.scrollTop + body.clientHeight) <= 100;
    this.win_.document.write(this.outputBuffer_.join(""));
    this.outputBuffer_.length = 0;
    if(scroll) {
      this.win_.scrollTo(0, 1000000)
    }
  }
};
goog.debug.DebugWindow.prototype.writeSavedMessages_ = function() {
  var messages = this.savedMessages_.getValues();
  for(var i = 0;i < messages.length;i++) {
    this.writeToLog_(messages[i])
  }
};
goog.debug.DebugWindow.prototype.openWindow_ = function() {
  if(this.win_ && !this.win_.closed || this.winOpening_) {
    return
  }var winpos = this.getCookie_("dbg", "0,0,800,500").split(","), x = Number(winpos[0]), y = Number(winpos[1]), w = Number(winpos[2]), h = Number(winpos[3]);
  this.winOpening_ = true;
  this.win_ = window.open("", "dbg" + this.identifier_, "width=" + w + ",height=" + h + ",toolbar=no,resizable=yes,scrollbars=yes,left=" + x + ",top=" + y + ",status=no,screenx=" + x + ",screeny=" + y);
  if(!this.win_) {
    if(!this.showedBlockedAlert_) {
      alert("Logger popup was blocked");
      this.showedBlockedAlert_ = true
    }
  }this.winOpening_ = false;
  if(this.win_) {
    this.writeInitialDocument_()
  }
};
goog.debug.DebugWindow.prototype.writeInitialDocument_ = function() {
  if(!this.win_) {
    return
  }this.win_.document.open();
  var html = '<style>*{font:normal 14px monospace;}.dbg-sev{color:#F00}.dbg-w{color:#E92}.dbg-sh{font-weight:bold;color:#000}.dbg-i{color:#666}.dbg-f{color:#999}.dbg-ev{color:#0A0}.dbg-m{color:#990}</style><hr><div class="dbg-ev" style="text-align:center">' + this.welcomeMessage_ + "<br><small>Logger: " + this.identifier_ + "</small></div><hr>";
  this.writeToLog_(html);
  this.writeSavedMessages_()
};
goog.debug.DebugWindow.prototype.setCookie_ = function(key, value) {
  key += this.identifier_;
  document.cookie = key + "=" + encodeURIComponent(value) + ";expires=" + (new Date(goog.now() + goog.debug.DebugWindow.COOKIE_TIME)).toUTCString()
};
goog.debug.DebugWindow.prototype.getCookie_ = function(key, opt_default) {
  key += this.identifier_;
  var cookie = String(document.cookie), start = cookie.indexOf(key + "=");
  if(start != -1) {
    var end = cookie.indexOf(";", start);
    return decodeURIComponent(cookie.substring(start + key.length + 1, end == -1 ? cookie.length : end))
  }else {
    return opt_default || ""
  }
};
goog.debug.DebugWindow.prototype.saveWindowPositionSize_ = function() {
  if(!this.win_ || this.win_.closed) {
    return
  }var x = this.win_.screenX || this.win_.screenLeft || 0, y = this.win_.screenY || this.win_.screenTop || 0, w = this.win_.outerWidth || 800, h = this.win_.outerHeight || 500;
  this.setCookie_("dbg", x + "," + y + "," + w + "," + h)
};goog.math = {};
goog.math.randomInt = function(a) {
  return Math.floor(Math.random() * a)
};
goog.math.uniformRandom = function(a, b) {
  return a + Math.random() * (b - a)
};
goog.math.clamp = function(a, min, max) {
  return Math.min(Math.max(a, min), max)
};
goog.math.modulo = function(a, b) {
  var r = a % b;
  return r * b < 0 ? r + b : r
};
goog.math.lerp = function(a, b, x) {
  return a + x * (b - a)
};
goog.math.Size = function(opt_w, opt_h) {
  this.width = goog.isDef(opt_w) ? Number(opt_w) : undefined;
  this.height = goog.isDef(opt_h) ? Number(opt_h) : undefined
};
goog.math.Size.prototype.clone = function() {
  return new goog.math.Size(this.width, this.height)
};
goog.math.Size.prototype.toString = function() {
  return"(" + this.width + " x " + this.height + ")"
};
goog.math.Size.equals = function(a, b) {
  if(a == b) {
    return true
  }if(!a || !b) {
    return false
  }return a.width == b.width && a.height == b.height
};
goog.math.Coordinate = function(opt_x, opt_y) {
  this.x = goog.isDef(opt_x) ? Number(opt_x) : undefined;
  this.y = goog.isDef(opt_y) ? Number(opt_y) : undefined
};
goog.math.Coordinate.prototype.clone = function() {
  return new goog.math.Coordinate(this.x, this.y)
};
goog.math.Coordinate.prototype.toString = function() {
  return"(" + this.x + ", " + this.y + ")"
};
goog.math.Coordinate.equals = function(a, b) {
  if(a == b) {
    return true
  }if(!a || !b) {
    return false
  }return a.x == b.x && a.y == b.y
};
goog.math.Coordinate.distance = function(a, b) {
  var dx = a.x - b.x, dy = a.y - b.y;
  return Math.sqrt(dx * dx + dy * dy)
};
goog.math.Coordinate.squaredDistance = function(a, b) {
  var dx = a.x - b.x, dy = a.y - b.y;
  return dx * dx + dy * dy
};
goog.math.Coordinate.difference = function(a, b) {
  return new goog.math.Coordinate(a.x - b.x, a.y - b.y)
};
goog.math.Range = function(a, b) {
  a = Number(a);
  b = Number(b);
  this.start = a < b ? a : b;
  this.end = a < b ? b : a
};
goog.math.Range.prototype.clone = function() {
  return new goog.math.Range(this.start, this.end)
};
goog.math.Range.prototype.toString = function() {
  return"[" + this.start + ", " + this.end + "]"
};
goog.math.Range.equals = function(a, b) {
  if(a == b) {
    return true
  }if(!a || !b) {
    return false
  }return a.start == b.start && a.end == b.end
};
goog.math.Range.intersection = function(a, b) {
  var c0 = Math.max(a.start, b.start), c1 = Math.min(a.end, b.end);
  return c0 <= c1 ? new goog.math.Range(c0, c1) : null
};
goog.math.Range.boundingRange = function(a, b) {
  return new goog.math.Range(Math.min(a.start, b.start), Math.max(a.end, b.end))
};
goog.math.Range.contains = function(a, b) {
  return a.start <= b.start && a.end >= b.end
};
goog.math.Rect = function(opt_x, opt_y, opt_w, opt_h) {
  this.left = goog.isDef(opt_x) ? Number(opt_x) : undefined;
  this.top = goog.isDef(opt_y) ? Number(opt_y) : undefined;
  this.width = goog.isDef(opt_w) ? Number(opt_w) : undefined;
  this.height = goog.isDef(opt_h) ? Number(opt_h) : undefined
};
goog.math.Rect.prototype.clone = function() {
  return new goog.math.Rect(this.left, this.top, this.width, this.height)
};
goog.math.Rect.prototype.toString = function() {
  return"(" + this.left + ", " + this.top + " - " + this.width + "w x " + this.height + "h)"
};
goog.math.Rect.equals = function(a, b) {
  if(a == b) {
    return true
  }if(!a || !b) {
    return false
  }return a.left == b.left && a.width == b.width && a.top == b.top && a.height == b.height
};
goog.math.Rect.intersection = function(a, b) {
  var x0 = Math.max(a.left, b.left), x1 = Math.min(a.left + a.width, b.left + b.width);
  if(x0 <= x1) {
    var y0 = Math.max(a.top, b.top), y1 = Math.min(a.top + a.height, b.top + b.height);
    if(y0 <= y1) {
      return new goog.math.Rect(x0, y0, x1 - x0, y1 - y0)
    }
  }return null
};
goog.math.Rect.difference = function(a, b) {
  if(!goog.math.Rect.intersection(a, b)) {
    return[a.clone()]
  }var result = [], top = a.top, height = a.height, ar = a.left + a.width, ab = a.top + a.height, br = b.left + b.width, bb = b.top + b.height;
  if(b.top > a.top) {
    result.push(new goog.math.Rect(a.left, a.top, a.width, b.top - a.top));
    top = b.top
  }if(bb < ab) {
    result.push(new goog.math.Rect(a.left, bb, a.width, ab - bb));
    height = bb - top
  }if(b.left > a.left) {
    result.push(new goog.math.Rect(a.left, top, b.left - a.left, height))
  }if(br < ar) {
    result.push(new goog.math.Rect(br, top, ar - br, height))
  }return result
};
goog.math.Rect.boundingRect = function(a, b) {
  if(!a || !b) {
    return null
  }var left = Math.min(a.left, b.left), top = Math.min(a.top, b.top), right = Math.max(a.left + a.width, b.left + b.width), bottom = Math.max(a.top + a.height, b.top + b.height);
  return new goog.math.Rect(left, top, right - left, bottom - top)
};
goog.math.Box = function(opt_top, opt_right, opt_bottom, opt_left) {
  this.top = goog.isDef(opt_top) ? Number(opt_top) : undefined;
  this.right = goog.isDef(opt_right) ? Number(opt_right) : undefined;
  this.bottom = goog.isDef(opt_bottom) ? Number(opt_bottom) : undefined;
  this.left = goog.isDef(opt_left) ? Number(opt_left) : undefined
};
goog.math.Box.boundingBox = function() {
  var box = new goog.math.Box(arguments[0].y, arguments[0].x, arguments[0].y, arguments[0].x);
  for(var i = 1;i < arguments.length;i++) {
    var coord = arguments[i];
    box.top = Math.min(box.top, coord.y);
    box.right = Math.max(box.right, coord.x);
    box.bottom = Math.max(box.bottom, coord.y);
    box.left = Math.min(box.left, coord.x)
  }return box
};
goog.math.Box.prototype.clone = function() {
  return new goog.math.Box(this.top, this.right, this.bottom, this.left)
};
goog.math.Box.prototype.toString = function() {
  return"(" + this.top + "t, " + this.right + "r, " + this.bottom + "b, " + this.left + "l)"
};
goog.math.Box.prototype.contains = function(coord) {
  return goog.math.Box.contains(this, coord)
};
goog.math.Box.prototype.expand = function(top, opt_right, opt_bottom, opt_left) {
  if(goog.isObject(top)) {
    this.top -= top.top;
    this.right += top.right;
    this.bottom += top.bottom;
    this.left -= top.left
  }else {
    this.top -= top;
    this.right += opt_right;
    this.bottom += opt_bottom;
    this.left -= opt_left
  }return this
};
goog.math.Box.equals = function(a, b) {
  if(a == b) {
    return true
  }if(!a || !b) {
    return false
  }return a.top == b.top && a.right == b.right && a.bottom == b.bottom && a.left == b.left
};
goog.math.Box.contains = function(box, coord) {
  if(!box || !coord) {
    return false
  }return coord.x >= box.left && coord.x <= box.right && coord.y >= box.top && coord.y <= box.bottom
};
goog.math.Box.distance = function(box, coord) {
  if(coord.x >= box.left && coord.x <= box.right) {
    if(coord.y >= box.top && coord.y <= box.bottom) {
      return 0
    }return coord.y < box.top ? box.top - coord.y : coord.y - box.bottom
  }if(coord.y >= box.top && coord.y <= box.bottom) {
    return coord.x < box.left ? box.left - coord.x : coord.x - box.right
  }return goog.math.Coordinate.distance(coord, new goog.math.Coordinate(coord.x < box.left ? box.left : box.right, coord.y < box.top ? box.top : box.bottom))
};
goog.math.standardAngle = function(angle) {
  return goog.math.modulo(angle, 360)
};
goog.math.toRadians = function(angleDegrees) {
  return angleDegrees * Math.PI / 180
};
goog.math.toDegrees = function(angleRadians) {
  return angleRadians * 180 / Math.PI
};
goog.math.angleDx = function(degrees, radius) {
  return radius * Math.cos(goog.math.toRadians(degrees))
};
goog.math.angleDy = function(degrees, radius) {
  return radius * Math.sin(goog.math.toRadians(degrees))
};
goog.math.angle = function(x1, y1, x2, y2) {
  return goog.math.standardAngle(goog.math.toDegrees(Math.atan2(y2 - y1, x2 - x1)))
};
goog.math.angleDifference = function(startAngle, endAngle) {
  var d = goog.math.standardAngle(endAngle) - goog.math.standardAngle(startAngle);
  if(d > 180) {
    d = d - 360
  }else if(d <= -180) {
    d = 360 + d
  }return d
};goog.userAgent = {};
(function() {
  var ua = navigator.userAgent, isOpera = typeof opera != "undefined", isIe = !isOpera && ua.indexOf("MSIE") != -1, isSafari = !isOpera && ua.indexOf("WebKit") != -1, isGecko = !isOpera && navigator.product == "Gecko" && !isSafari, isCamino = isGecko && navigator.vendor == "Camino", isKonqueror = !isOpera && ua.indexOf("Konqueror") != -1, isKhtml = isKonqueror || isSafari, version, re;
  if(isOpera) {
    version = opera.version()
  }else {
    if(isGecko) {
      re = /rv\:([^\);]+)(\)|;)/
    }else if(isIe) {
      re = /MSIE\s+([^\);]+)(\)|;)/
    }else if(isSafari) {
      re = /WebKit\/(\S+)/
    }else if(isKonqueror) {
      re = /Konqueror\/([^\);]+)(\)|;)/
    }if(re) {
      re.test(ua);
      version = RegExp.$1
    }
  }var platform = navigator.platform, isMac = platform.indexOf("Mac") != -1, isWindows = platform.indexOf("Win") != -1, isLinux = platform.indexOf("Linux") != -1;
  goog.userAgent.OPERA = isOpera;
  goog.userAgent.IE = isIe;
  goog.userAgent.GECKO = isGecko;
  goog.userAgent.CAMINO = isCamino;
  goog.userAgent.KONQUEROR = isKonqueror;
  goog.userAgent.SAFARI = isSafari;
  goog.userAgent.KHTML = isKhtml;
  goog.userAgent.VERSION = version;
  goog.userAgent.PLATFORM = navigator.platform;
  goog.userAgent.MAC = isMac;
  goog.userAgent.WINDOWS = isWindows;
  goog.userAgent.LINUX = isLinux
})();
goog.userAgent.compare = function(aV1, aV2) {
  if(!isNaN(aV1) && !isNaN(aV2)) {
    return aV1 - aV2
  }var v1 = aV1.split("."), v2 = aV2.split("."), numSubversions = Math.min(v1.length, v2.length);
  for(var i = 0;i < numSubversions;i++) {
    if(typeof v2[i] == "undefined") {
      return 1
    }if(typeof v1[i] == "undefined") {
      return-1
    }if(!isNaN(v1[i]) && isNaN(v2[i]) && v1[i] == parseInt(v2[i], 10)) {
      return 1
    }if(isNaN(v1[i]) && !isNaN(v2[i]) && parseInt(v1[i], 10) == v2[i]) {
      return-1
    }if(v2[i] > v1[i]) {
      return-1
    }else if(v2[i] < v1[i]) {
      return 1
    }
  }return 0
};
goog.userAgent.isVersion = function(version) {
  return goog.userAgent.compare(goog.userAgent.VERSION, version) >= 0
};goog.dom = {};
goog.dom.NodeType = {ELEMENT:1, ATTRIBUTE:2, TEXT:3, CDATA_SECTION:4, ENTITY_REFERENCE:5, ENTITY:6, PROCESSING_INSTRUCTION:7, COMMENT:8, DOCUMENT:9, DOCUMENT_TYPE:10, DOCUMENT_FRAGMENT:11, NOTATION:12};
goog.dom.getDefaultDomHelper_ = function() {
  if(!goog.dom.defaultDomHelper_) {
    goog.dom.defaultDomHelper_ = new goog.dom.DomHelper
  }return goog.dom.defaultDomHelper_
};
goog.dom.getDomHelper = function(opt_element) {
  return opt_element ? new goog.dom.DomHelper(goog.dom.getOwnerDocument(opt_element)) : goog.dom.getDefaultDomHelper_()
};
goog.dom.getDocument = function() {
  return goog.dom.getDefaultDomHelper_().getDocument()
};
goog.dom.getElement = function(element) {
  return goog.dom.getDefaultDomHelper_().getElement(element)
};
goog.dom.$ = goog.dom.getElement;
goog.dom.getElementsByTagNameAndClass = function(opt_tag, opt_class, opt_el) {
  return goog.dom.getDefaultDomHelper_().getElementsByTagNameAndClass(opt_tag, opt_class, opt_el)
};
goog.dom.$$ = goog.dom.getElementsByTagNameAndClass;
goog.dom.setProperties = function(element, properties) {
  goog.object.forEach(properties, function(val, key) {
    if(key == "style") {
      element.style.cssText = val
    }else if(key == "class") {
      element.className = val
    }else if(key == "for") {
      element.htmlFor = val
    }else if(key in goog.dom.DIRECT_ATTRIBUTE_MAP_) {
      element.setAttribute(goog.dom.DIRECT_ATTRIBUTE_MAP_[key], val)
    }else {
      element[key] = val
    }
  })
};
goog.dom.DIRECT_ATTRIBUTE_MAP_ = {cellpadding:"cellPadding", cellspacing:"cellSpacing", colspan:"colSpan", rowspan:"rowSpan", valign:"vAlign", height:"height", width:"width", frameborder:"frameBorder"};
goog.dom.getViewportSize = function(opt_window) {
  var win = opt_window || goog.global || window, doc = win.document, el = (goog.userAgent.SAFARI || doc.compatMode == "CSS1Compat") && !goog.userAgent.OPERA ? doc.documentElement : doc.body;
  return new goog.math.Size(el.clientWidth, el.clientHeight)
};
goog.dom.getPageScroll = function(opt_window) {
  var win = opt_window || goog.global || window, doc = win.document, x, y;
  if(doc.compatMode == "CSS1Compat") {
    x = doc.documentElement.scrollLeft;
    y = doc.documentElement.scrollTop
  }else {
    x = doc.body.scrollLeft;
    y = doc.body.scrollTop
  }return new goog.math.Coordinate(x, y)
};
goog.dom.getWindow = function(doc) {
  return doc.parentWindow || doc.defaultView
};
goog.dom.createDom = function(tagName, opt_attributes) {
  var dh = goog.dom.getDefaultDomHelper_();
  return dh.createDom.apply(dh, arguments)
};
goog.dom.$dom = goog.dom.createDom;
goog.dom.createElement = function(name) {
  return goog.dom.getDefaultDomHelper_().createElement(name)
};
goog.dom.createTextNode = function(content) {
  return goog.dom.getDefaultDomHelper_().createTextNode(content)
};
goog.dom.htmlToDocumentFragment = function(htmlString) {
  return goog.dom.getDefaultDomHelper_().htmlToDocumentFragment(htmlString)
};
goog.dom.appendChild = function(parent, child) {
  parent.appendChild(child)
};
goog.dom.removeChildren = function(node) {
  var child;
  while(child = node.firstChild) {
    node.removeChild(child)
  }
};
goog.dom.insertSiblingBefore = function(newNode, refNode) {
  if(refNode.parentNode) {
    refNode.parentNode.insertBefore(newNode, refNode)
  }
};
goog.dom.insertSiblingAfter = function(newNode, refNode) {
  if(refNode.parentNode) {
    refNode.parentNode.insertBefore(newNode, refNode.nextSibling)
  }
};
goog.dom.removeNode = function(node) {
  if(node.parentNode) {
    node.parentNode.removeChild(node)
  }
};
goog.dom.getFirstElementChild = function(node) {
  return goog.dom.getNextElementNode_(node.firstChild, true)
};
goog.dom.getLastElementChild = function(node) {
  return goog.dom.getNextElementNode_(node.lastChild, false)
};
goog.dom.getNextElementSibling = function(node) {
  return goog.dom.getNextElementNode_(node.nextSibling, true)
};
goog.dom.getPreviousElementSibling = function(node) {
  return goog.dom.getNextElementNode_(node.previousSibling, false)
};
goog.dom.getNextElementNode_ = function(node, forward) {
  while(node && node.nodeType != goog.dom.NodeType.ELEMENT) {
    node = forward ? node.nextSibling : node.previousSibling
  }return node
};
goog.dom.isNodeLike = function(obj) {
  return goog.isObject(obj) && obj.nodeType > 0
};
goog.dom.BAD_CONTAINS_SAFARI_ = goog.userAgent.SAFARI && goog.userAgent.compare(goog.userAgent.VERSION, "521") <= 0;
goog.dom.contains = function(parent, descendant) {
  if(typeof parent.contains != "undefined" && !goog.dom.BAD_CONTAINS_SAFARI_ && descendant.nodeType == goog.dom.NodeType.ELEMENT) {
    return parent == descendant || parent.contains(descendant)
  }if(typeof parent.compareDocumentPosition != "undefined") {
    return parent == descendant || Boolean(parent.compareDocumentPosition(descendant) & 16)
  }while(descendant && parent != descendant) {
    descendant = descendant.parentNode
  }return descendant == parent
};
goog.dom.compareNodeOrder = function(node1, node2) {
  if(node1 == node2) {
    return 0
  }if(node1.compareDocumentPosition) {
    return node1.compareDocumentPosition(node2) & 2 ? 1 : -1
  }if("sourceIndex" in node1 || node1.parentNode && "sourceIndex" in node1.parentNode) {
    var isElement1 = node1.nodeType == goog.dom.NodeType.ELEMENT, isElement2 = node2.nodeType == goog.dom.NodeType.ELEMENT, index1 = isElement1 ? node1.sourceIndex : node1.parentNode.sourceIndex, index2 = isElement2 ? node2.sourceIndex : node2.parentNode.sourceIndex;
    if(index1 != index2) {
      return index1 - index2
    }else {
      if(isElement1) {
        return-1
      }if(isElement2) {
        return 1
      }var s = node2;
      while(s = s.previousSibling) {
        if(s == node1) {
          return-1
        }
      }return 1
    }
  }var doc = goog.dom.getOwnerDocument(node1), range1, range2;
  range1 = doc.createRange();
  range1.selectNode(node1);
  range1.collapse(true);
  range2 = doc.createRange();
  range2.selectNode(node2);
  range2.collapse(true);
  return range1.compareBoundaryPoints(Range.START_TO_END, range2)
};
goog.dom.getOwnerDocument = function(node) {
  return node.nodeType == goog.dom.NodeType.DOCUMENT ? node : node.ownerDocument || node.document
};
goog.dom.getFrameContentDocument = function(frame) {
  return goog.userAgent.SAFARI ? frame.document || frame.contentWindow.document : frame.contentDocument || frame.contentWindow.document
};
goog.dom.setTextContent = function(element, text) {
  if("textContent" in element) {
    element.textContent = text
  }else if(element.firstChild && element.firstChild.nodeType == goog.dom.NodeType.TEXT) {
    while(element.lastChild != element.firstChild) {
      element.removeChild(element.lastChild)
    }element.firstChild.data = text
  }else {
    while(element.hasChildNodes()) {
      element.removeChild(element.lastChild)
    }var doc = goog.dom.getOwnerDocument(element);
    element.appendChild(doc.createTextNode(text))
  }
};
goog.dom.findNode = function(root, p) {
  var rv = [];
  goog.dom.findNodes_(root, p, rv, true);
  return rv.length ? rv[0] : undefined
};
goog.dom.findNodes = function(root, p) {
  var rv = [];
  goog.dom.findNodes_(root, p, rv, false);
  return rv
};
goog.dom.findNodes_ = function(root, p, rv, findOne) {
  if(root != null) {
    for(var i = 0, child;child = root.childNodes[i];i++) {
      if(p(child)) {
        rv.push(child);
        if(findOne) {
          return
        }
      }goog.dom.findNodes_(child, p, rv, findOne)
    }
  }
};
goog.dom.TAGS_TO_IGNORE = {SCRIPT:1, STYLE:1, HEAD:1, IFRAME:1, OBJECT:1};
goog.dom.PREDEFINED_TAG_VALUES = {IMG:" ", BR:"\n"};
goog.dom.getTextContent = function(node) {
  if(goog.userAgent.IE && "innerText" in node) {
    return goog.string.canonicalizeNewlines(node.innerText)
  }var buf = [];
  goog.dom.getTextContent_(node, buf, true);
  var rv = buf.join("").replace(/ +/g, " ");
  if(rv != " ") {
    rv = rv.replace(/^\s*/, "")
  }return rv
};
goog.dom.getRawTextContent = function(node) {
  var buf = [];
  goog.dom.getTextContent_(node, buf, false);
  return buf.join("")
};
goog.dom.getTextContent_ = function(node, buf, normalizeWhitespace) {
  if(node.nodeName in goog.dom.TAGS_TO_IGNORE) {
  }else if(node.nodeType == goog.dom.NodeType.TEXT) {
    if(normalizeWhitespace) {
      buf.push(String(node.nodeValue).replace(/(\r\n|\r|\n)/g, ""))
    }else {
      buf.push(node.nodeValue)
    }
  }else if(node.nodeName in goog.dom.PREDEFINED_TAG_VALUES) {
    buf.push(goog.dom.PREDEFINED_TAG_VALUES[node.nodeName])
  }else {
    var child = node.firstChild;
    while(child) {
      goog.dom.getTextContent_(child, buf, normalizeWhitespace);
      child = child.nextSibling
    }
  }
};
goog.dom.getNodeTextLength = function(node) {
  return goog.dom.getTextContent(node).length
};
goog.dom.getNodeTextOffset = function(node, opt_offsetParent) {
  var root = opt_offsetParent || goog.dom.getOwnerDocument(node).body, buf = [];
  while(node && node != root) {
    var cur = node;
    while(cur = cur.previousSibling) {
      buf.unshift(goog.dom.getTextContent(cur))
    }node = node.parentNode
  }return goog.string.trimLeft(buf.join("")).replace(/ +/g, " ").length
};
goog.dom.getNodeAtOffset = function(parent, offset, opt_result) {
  var stack = [parent], pos = 0, cur;
  while(stack.length > 0 && pos < offset) {
    cur = stack.pop();
    if(cur.nodeName in goog.dom.TAGS_TO_IGNORE) {
    }else if(cur.nodeType == goog.dom.NodeType.TEXT) {
      var text = cur.nodeValue.replace(/(\r\n|\r|\n)/g, "").replace(/ +/g, " ");
      pos += text.length
    }else if(cur.nodeName in goog.dom.PREDEFINED_TAG_VALUES) {
      pos += goog.dom.PREDEFINED_TAG_VALUES(cur.nodeName).length
    }else {
      for(var i = cur.childNodes.length - 1;i >= 0;i--) {
        stack.push(cur.childNodes[i])
      }
    }
  }if(goog.isObject(opt_result)) {
    opt_result.remainder = cur ? cur.nodeValue.length + offset - pos - 1 : 0;
    opt_result.node = cur
  }return cur
};
goog.dom.DomHelper = function(opt_document) {
  this.document_ = opt_document || goog.global.document || document
};
goog.dom.DomHelper.prototype.getDomHelper = goog.dom.getDomHelper;
goog.dom.DomHelper.prototype.getDocument = function() {
  return this.document_
};
goog.dom.DomHelper.prototype.getElement = function(element) {
  if(goog.isString(element)) {
    return this.document_.getElementById(element)
  }else {
    return element
  }
};
goog.dom.DomHelper.prototype.$ = goog.dom.DomHelper.prototype.getElement;
goog.dom.DomHelper.prototype.getElementsByTagNameAndClass = function(opt_tag, opt_class, opt_el) {
  var tag = opt_tag || "*", parent = opt_el || this.document_, els = parent.getElementsByTagName(tag);
  if(opt_class) {
    return goog.array.filter(els, function(el) {
      return goog.array.contains(el.className.split(" "), opt_class)
    })
  }else {
    return els
  }
};
goog.dom.DomHelper.prototype.$$ = goog.dom.DomHelper.prototype.getElementsByTagNameAndClass;
goog.dom.DomHelper.prototype.setProperties = goog.dom.setProperties;
goog.dom.DomHelper.prototype.getViewportSize = goog.dom.getViewportSize;
goog.dom.DomHelper.prototype.createDom = function(tagName, opt_attributes) {
  if(goog.userAgent.IE && opt_attributes && opt_attributes.name) {
    tagName = "<" + tagName + ' name="' + goog.string.htmlEscape(opt_attributes.name) + '">'
  }var element = this.createElement(tagName);
  if(opt_attributes) {
    goog.dom.setProperties(element, opt_attributes)
  }if(arguments.length > 2) {
    function childHandler(child) {
      if(child) {
        this.appendChild(element, goog.isString(child) ? this.createTextNode(child) : child)
      }
    };
    for(var i = 2;i < arguments.length;i++) {
      var arg = arguments[i];
      if((goog.isArrayLike(arg) || goog.userAgent.SAFARI && typeof arg == "function" && typeof arg.length == "number") && !goog.dom.isNodeLike(arg)) {
        goog.array.forEach(goog.isArray(arg) ? arg : goog.array.clone(arg), childHandler, this)
      }else {
        childHandler.call(this, arg)
      }
    }
  }return element
};
goog.dom.DomHelper.prototype.$dom = goog.dom.DomHelper.prototype.createDom;
goog.dom.DomHelper.prototype.createElement = function(name) {
  return this.document_.createElement(name)
};
goog.dom.DomHelper.prototype.createTextNode = function(content) {
  return this.document_.createTextNode(content)
};
goog.dom.DomHelper.prototype.htmlToDocumentFragment = function(htmlString) {
  var tempDiv = this.document_.createElement("div");
  tempDiv.innerHTML = htmlString;
  if(tempDiv.childNodes.length == 1) {
    return tempDiv.firstChild
  }else {
    var fragment = this.document_.createDocumentFragment();
    while(tempDiv.firstChild) {
      fragment.appendChild(tempDiv.firstChild)
    }return fragment
  }
};
goog.dom.DomHelper.prototype.appendChild = goog.dom.appendChild;
goog.dom.DomHelper.prototype.removeChildren = goog.dom.removeChildren;
goog.dom.DomHelper.prototype.insertSiblingBefore = goog.dom.insertSiblingBefore;
goog.dom.DomHelper.prototype.insertSiblingAfter = goog.dom.insertSiblingAfter;
goog.dom.DomHelper.prototype.removeNode = goog.dom.removeNode;
goog.dom.DomHelper.prototype.getFirstElementChild = goog.dom.getFirstElementChild;
goog.dom.DomHelper.prototype.getLastElementChild = goog.dom.getLastElementChild;
goog.dom.DomHelper.prototype.getNextElementSibling = goog.dom.getNextElementSibling;
goog.dom.DomHelper.prototype.getPreviousElementSibling = goog.dom.getPreviousElementSibling;
goog.dom.DomHelper.prototype.isNodeLike = goog.dom.isNodeLike;
goog.dom.DomHelper.prototype.contains = goog.dom.contains;
goog.dom.DomHelper.prototype.getOwnerDocument = goog.dom.getOwnerDocument;
goog.dom.DomHelper.prototype.getFrameContentDocument = goog.dom.getFrameContentDocument;
goog.dom.DomHelper.prototype.setTextContent = goog.dom.setTextContent;
goog.dom.DomHelper.prototype.findNode = goog.dom.findNode;
goog.dom.DomHelper.prototype.findNodes = goog.dom.findNodes;
goog.dom.DomHelper.prototype.getTextContent = goog.dom.getTextContent;
goog.dom.DomHelper.prototype.getNodeTextLength = goog.dom.getNodeTextLength;
goog.dom.DomHelper.prototype.getNodeTextOffset = goog.dom.getNodeTextOffset;goog.Disposable = function() {
};
goog.Disposable.prototype.disposed_ = false;
goog.Disposable.prototype.getDisposed = function() {
  return this.disposed_
};
goog.Disposable.prototype.dispose = function() {
  if(!this.disposed_) {
    this.disposed_ = true
  }
};
goog.dispose = function(obj) {
  if(typeof obj.dispose == "function") {
    obj.dispose()
  }
};goog.events = {};
goog.events.Event = function(type, opt_target) {
  this.type = type;
  this.target = opt_target;
  this.currentTarget = this.target
};
goog.events.Event.inherits(goog.Disposable);
goog.events.Event.prototype.propagationStopped_ = false;
goog.events.Event.prototype.returnValue_ = true;
goog.events.Event.prototype.stopPropagation = function() {
  this.propagationStopped_ = true
};
goog.events.Event.prototype.preventDefault = function() {
  this.returnValue_ = false
};goog.events.BrowserEvent = function(opt_e, opt_currentTarget) {
  if(opt_e) {
    this.init(opt_e, opt_currentTarget)
  }
};
goog.events.BrowserEvent.inherits(goog.events.Event);
goog.events.BrowserEvent.MouseButton = {LEFT:0, MIDDLE:1, RIGHT:2};
goog.events.BrowserEvent.IEButtonMap_ = [1, 4, 2];
goog.events.BrowserEvent.prototype.type = null;
goog.events.BrowserEvent.prototype.target = null;
goog.events.BrowserEvent.prototype.currentTarget = null;
goog.events.BrowserEvent.prototype.relatedTarget = null;
goog.events.BrowserEvent.prototype.offsetX = 0;
goog.events.BrowserEvent.prototype.offsetY = 0;
goog.events.BrowserEvent.prototype.clientX = 0;
goog.events.BrowserEvent.prototype.clientY = 0;
goog.events.BrowserEvent.prototype.screenX = 0;
goog.events.BrowserEvent.prototype.screenY = 0;
goog.events.BrowserEvent.prototype.button = 0;
goog.events.BrowserEvent.prototype.keyCode = 0;
goog.events.BrowserEvent.prototype.charCode = 0;
goog.events.BrowserEvent.prototype.ctrlKey = false;
goog.events.BrowserEvent.prototype.altKey = false;
goog.events.BrowserEvent.prototype.shiftKey = false;
goog.events.BrowserEvent.prototype.metaKey = false;
goog.events.BrowserEvent.prototype.event_ = null;
goog.events.BrowserEvent.prototype.init = function(e, opt_currentTarget) {
  this.type = e.type;
  this.target = e.target || e.srcElement;
  this.currentTarget = opt_currentTarget;
  if(goog.isDef(e.relatedTarget)) {
    this.relatedTarget = e.relatedTarget
  }else if(this.type == goog.events.EventType.MOUSEOVER) {
    this.relatedTarget = e.fromElement
  }else if(this.type == goog.events.EventType.MOUSEOUT) {
    this.relatedTarget = e.toElement
  }else {
    this.relatedTarget = null
  }this.offsetX = goog.isDef(e.layerX) ? e.layerX : e.offsetX;
  this.offsetY = goog.isDef(e.layerY) ? e.layerY : e.offsetY;
  this.clientX = goog.isDef(e.clientX) ? e.clientX : e.pageX;
  this.clientY = goog.isDef(e.clientY) ? e.clientY : e.pageY;
  this.screenX = e.screenX || 0;
  this.screenY = e.screenY || 0;
  this.button = e.button;
  this.keyCode = e.keyCode || 0;
  this.charCode = e.charCode || (this.type == goog.events.EventType.KEYPRESS ? e.keyCode : 0);
  this.ctrlKey = e.ctrlKey;
  this.altKey = e.altKey;
  this.shiftKey = e.shiftKey;
  this.metaKey = e.metaKey;
  this.event_ = e;
  this.returnValue_ = null;
  this.propagationStopped_ = null
};
goog.events.BrowserEvent.prototype.stopPropagation = function() {
  this.propagationStopped_ = true;
  if(this.event_.stopPropagation) {
    this.event_.stopPropagation()
  }else {
    this.event_.cancelBubble = true
  }
};
goog.events.BrowserEvent.prototype.preventDefault = function() {
  this.returnValue_ = false;
  if(!this.event_.preventDefault) {
    this.event_.returnValue = false;
    try {
      this.event_.keyCode = -1
    }catch(ex) {
    }
  }else {
    this.event_.preventDefault()
  }
};
goog.events.BrowserEvent.prototype.dispose = function() {
  if(!this.getDisposed()) {
    goog.events.Event.prototype.dispose.call(this);
    this.event_ = null
  }
};goog.events.Listener = function() {
};
goog.events.Listener.counter_ = 0;
goog.events.Listener.prototype.isFunctionListener_ = null;
goog.events.Listener.prototype.listener = null;
goog.events.Listener.prototype.proxy = null;
goog.events.Listener.prototype.src = null;
goog.events.Listener.prototype.type = null;
goog.events.Listener.prototype.capture = null;
goog.events.Listener.prototype.handler = null;
goog.events.Listener.prototype.key = 0;
goog.events.Listener.prototype.removed = false;
goog.events.Listener.prototype.callOnce = false;
goog.events.Listener.prototype.init = function(listener, proxy, src, type, capture, handler) {
  if(goog.isFunction(listener)) {
    this.isFunctionListener_ = true
  }else if(listener && listener.handleEvent && goog.isFunction(listener.handleEvent)) {
    this.isFunctionListener_ = false
  }else {
    throw Error("Invalid listener argument");
  }this.listener = listener;
  this.proxy = proxy;
  this.src = src;
  this.type = type;
  this.capture = !(!capture);
  this.handler = handler;
  this.callOnce = false;
  this.key = ++goog.events.Listener.counter_;
  this.removed = false
};
goog.events.Listener.prototype.handleEvent = function(eventObject) {
  if(this.isFunctionListener_) {
    return this.listener.call(this.handler || this.src, eventObject)
  }return this.listener.handleEvent.call(this.listener, eventObject)
};goog.structs.SimplePool = function(initialCount, maxCount) {
  goog.Disposable.call(this);
  this.maxCount_ = maxCount;
  this.freeQueue_ = [];
  for(var i = 0;i < initialCount;i++) {
    this.releaseObject(this.createObject())
  }
};
goog.structs.SimplePool.inherits(goog.Disposable);
goog.structs.SimplePool.prototype.createObjectFn_ = null;
goog.structs.SimplePool.prototype.disposeObjectFn_ = null;
goog.structs.SimplePool.prototype.setCreateObjectFn = function(createObjectFn) {
  this.createObjectFn_ = createObjectFn
};
goog.structs.SimplePool.prototype.setDisposeObjectFn = function(disposeObjectFn) {
  this.disposeObjectFn_ = disposeObjectFn
};
goog.structs.SimplePool.prototype.getObject = function() {
  if(this.freeQueue_.length) {
    return this.freeQueue_.pop()
  }return this.createObject()
};
goog.structs.SimplePool.prototype.releaseObject = function(obj) {
  if(this.freeQueue_.length < this.maxCount_) {
    this.freeQueue_.push(obj)
  }else {
    this.disposeObject(obj)
  }
};
goog.structs.SimplePool.prototype.createObject = function() {
  if(this.createObjectFn_) {
    return this.createObjectFn_()
  }else {
    return{}
  }
};
goog.structs.SimplePool.prototype.disposeObject = function(obj) {
  if(this.disposeObjectFn_) {
    this.disposeObjectFn_(obj)
  }else {
    if(goog.isFunction(obj.dispose)) {
      obj.dispose()
    }else {
      for(var i in obj) {
        delete obj[i]
      }
    }
  }
};
goog.structs.SimplePool.prototype.dispose = function() {
  if(!this.getDisposed()) {
    goog.structs.SimplePool.superClass_.dispose.call(this);
    var freeQueue = this.freeQueue_;
    while(freeQueue.length) {
      this.disposeObject(freeQueue.pop())
    }this.freeQueue_ = null
  }
};goog.events.listeners_ = {};
goog.events.listenerTree_ = {};
goog.events.sources_ = {};
goog.events.OBJECT_POOL_INITIAL_COUNT = 0;
goog.events.OBJECT_POOL_MAX_COUNT = 600;
goog.events.objectPool_ = new goog.structs.SimplePool(goog.events.OBJECT_POOL_INITIAL_COUNT, goog.events.OBJECT_POOL_MAX_COUNT);
goog.events.objectPool_.setCreateObjectFn(function() {
  return{count_:0}
});
goog.events.objectPool_.setDisposeObjectFn(function(obj) {
  obj.count_ = 0
});
goog.events.ARRAY_POOL_INITIAL_COUNT = 0;
goog.events.ARRAY_POOL_MAX_COUNT = 600;
goog.events.arrayPool_ = new goog.structs.SimplePool(goog.events.ARRAY_POOL_INITIAL_COUNT, goog.events.ARRAY_POOL_MAX_COUNT);
goog.events.arrayPool_.setCreateObjectFn(function() {
  return[]
});
goog.events.arrayPool_.setDisposeObjectFn(function(obj) {
  obj.length = 0;
  delete obj.locked_;
  delete obj.needsCleanup_
});
goog.events.HANDLE_EVENT_PROXY_POOL_INITIAL_COUNT = 0;
goog.events.HANDLE_EVENT_PROXY_POOL_MAX_COUNT = 600;
goog.events.handleEventProxyPool_ = new goog.structs.SimplePool(goog.events.HANDLE_EVENT_PROXY_POOL_INITIAL_COUNT, goog.events.HANDLE_EVENT_PROXY_POOL_MAX_COUNT);
goog.events.handleEventProxyPool_.setCreateObjectFn(function() {
  var f = function(eventObject) {
    return goog.events.handleBrowserEvent_.call(f.src, f.key, eventObject)
  };
  return f
});
goog.events.LISTENER_POOL_INITIAL_COUNT = 0;
goog.events.LISTENER_POOL_MAX_COUNT = 600;
goog.events.createListenerFunction_ = function() {
  return new goog.events.Listener
};
goog.events.listenerPool_ = new goog.structs.SimplePool(goog.events.LISTENER_POOL_INITIAL_COUNT, goog.events.LISTENER_POOL_MAX_COUNT);
goog.events.listenerPool_.setCreateObjectFn(goog.events.createListenerFunction_);
goog.events.EVENT_POOL_INITIAL_COUNT = 0;
goog.events.EVENT_POOL_MAX_COUNT = 600;
goog.events.createEventFunction_ = function() {
  return new goog.events.BrowserEvent
};
goog.events.createEventPool_ = function() {
  var eventPool = null;
  if(goog.userAgent.IE) {
    eventPool = new goog.structs.SimplePool(goog.events.EVENT_POOL_INITIAL_COUNT, goog.events.EVENT_POOL_MAX_COUNT);
    eventPool.setCreateObjectFn(goog.events.createEventFunction_)
  }return eventPool
};
goog.events.eventPool_ = goog.events.createEventPool_();
goog.events.onString_ = "on";
goog.events.onStringMap_ = {};
goog.events.keySeparator_ = "_";
goog.events.listen = function(src, type, listener, opt_capt, opt_handler) {
  if(goog.isArray(type)) {
    for(var i = 0;i < type.length;i++) {
      goog.events.listen(src, type[i], listener, opt_capt, opt_handler)
    }return null
  }var capture = !(!opt_capt), map = goog.events.listenerTree_;
  if(!(type in map)) {
    map[type] = goog.events.objectPool_.getObject()
  }map = map[type];
  if(!(capture in map)) {
    map[capture] = goog.events.objectPool_.getObject();
    map.count_++
  }map = map[capture];
  var srcHashCode = goog.getHashCode(src), listenerArray, listenerObj;
  if(!map[srcHashCode]) {
    listenerArray = (map[srcHashCode] = goog.events.arrayPool_.getObject());
    map.count_++
  }else {
    listenerArray = map[srcHashCode];
    for(var i = 0;i < listenerArray.length;i++) {
      listenerObj = listenerArray[i];
      if(listenerObj.listener == listener && listenerObj.handler == opt_handler) {
        if(listenerObj.removed) {
          break
        }return listenerArray[i].key
      }
    }
  }var proxy = goog.events.handleEventProxyPool_.getObject();
  proxy.src = src;
  listenerObj = goog.events.listenerPool_.getObject();
  listenerObj.init(listener, proxy, src, type, capture, opt_handler);
  var key = listenerObj.key;
  proxy.key = key;
  listenerArray.push(listenerObj);
  goog.events.listeners_[key] = listenerObj;
  if(!goog.events.sources_[srcHashCode]) {
    goog.events.sources_[srcHashCode] = goog.events.arrayPool_.getObject()
  }goog.events.sources_[srcHashCode].push(listenerObj);
  if(src.addEventListener) {
    if(src == goog.global || !src.customEvent_) {
      src.addEventListener(type, proxy, capture)
    }
  }else {
    src.attachEvent(goog.events.getOnString_(type), proxy)
  }return key
};
goog.events.listenOnce = function(src, type, listener, opt_capt, opt_handler) {
  if(goog.isArray(type)) {
    for(var i = 0;i < type.length;i++) {
      goog.events.listenOnce(src, type[i], listener, opt_capt, opt_handler)
    }return null
  }var key = goog.events.listen(src, type, listener, opt_capt, opt_handler), listenerObj = goog.events.listeners_[key];
  listenerObj.callOnce = true;
  return key
};
goog.events.unlisten = function(src, type, listener, opt_capt, opt_handler) {
  if(goog.isArray(type)) {
    for(var i = 0;i < type.length;i++) {
      goog.events.unlisten(src, type[i], listener, opt_capt, opt_handler)
    }return null
  }var capture = !(!opt_capt), listenerArray = goog.events.getListeners_(src, type, capture);
  if(!listenerArray) {
    return false
  }for(var i = 0;i < listenerArray.length;i++) {
    if(listenerArray[i].listener == listener && listenerArray[i].capture == capture && listenerArray[i].handler == opt_handler) {
      return goog.events.unlistenByKey(listenerArray[i].key)
    }
  }return false
};
goog.events.unlistenByKey = function(key) {
  if(!goog.events.listeners_[key]) {
    return false
  }var listener = goog.events.listeners_[key];
  if(listener.removed) {
    return false
  }var src = listener.src, type = listener.type, proxy = listener.proxy, capture = listener.capture;
  if(src.removeEventListener) {
    if(src == goog.global || !src.customEvent_) {
      src.removeEventListener(type, proxy, capture)
    }
  }else if(src.detachEvent) {
    src.detachEvent(goog.events.getOnString_(type), proxy)
  }var srcHashCode = goog.getHashCode(src), listenerArray = goog.events.listenerTree_[type][capture][srcHashCode];
  if(goog.events.sources_[srcHashCode]) {
    var sourcesArray = goog.events.sources_[srcHashCode];
    goog.array.remove(sourcesArray, listener);
    if(sourcesArray.length == 0) {
      delete goog.events.sources_[srcHashCode]
    }
  }listener.removed = true;
  listenerArray.needsCleanup_ = true;
  goog.events.cleanUp_(type, capture, srcHashCode, listenerArray);
  delete goog.events.listeners_[key];
  return true
};
goog.events.cleanUp_ = function(type, capture, srcHashCode, listenerArray) {
  if(!listenerArray.locked_) {
    if(listenerArray.needsCleanup_) {
      for(var oldIndex = 0, newIndex = 0;oldIndex < listenerArray.length;oldIndex++) {
        if(listenerArray[oldIndex].removed) {
          goog.events.listenerPool_.releaseObject(listenerArray[oldIndex]);
          continue
        }if(oldIndex != newIndex) {
          listenerArray[newIndex] = listenerArray[oldIndex]
        }newIndex++
      }listenerArray.length = newIndex;
      listenerArray.needsCleanup_ = false;
      if(newIndex == 0) {
        goog.events.arrayPool_.releaseObject(listenerArray);
        delete goog.events.listenerTree_[type][capture][srcHashCode];
        goog.events.listenerTree_[type][capture].count_--;
        if(goog.events.listenerTree_[type][capture].count_ == 0) {
          goog.events.objectPool_.releaseObject(goog.events.listenerTree_[type][capture]);
          delete goog.events.listenerTree_[type][capture];
          goog.events.listenerTree_[type].count_--
        }if(goog.events.listenerTree_[type].count_ == 0) {
          goog.events.objectPool_.releaseObject(goog.events.listenerTree_[type]);
          delete goog.events.listenerTree_[type]
        }
      }
    }
  }
};
goog.events.removeAll = function(opt_obj, opt_type, opt_capt) {
  var count = 0, noObj = opt_obj == null, noType = opt_type == null, noCapt = opt_capt == null;
  opt_capt = !(!opt_capt);
  if(!noObj) {
    var srcHashCode = goog.getHashCode(opt_obj);
    if(goog.events.sources_[srcHashCode]) {
      var sourcesArray = goog.events.sources_[srcHashCode];
      for(var i = sourcesArray.length - 1;i >= 0;i--) {
        var listener = sourcesArray[i];
        if((noType || opt_type == listener.type) && (noCapt || opt_capt == listener.capture)) {
          goog.events.unlistenByKey(listener.key);
          count++
        }
      }
    }
  }else {
    goog.object.forEach(goog.events.sources_, function(listeners) {
      for(var i = listeners.length - 1;i >= 0;i--) {
        var listener = listeners[i];
        if((noType || opt_type == listener.type) && (noCapt || opt_capt == listener.capture)) {
          goog.events.unlistenByKey(listener.key);
          count++
        }
      }
    })
  }return count
};
goog.events.getListeners = function(obj, type, capture) {
  return goog.events.getListeners_(obj, type, capture) || []
};
goog.events.getListeners_ = function(obj, type, capture) {
  var map = goog.events.listenerTree_;
  if(type in map) {
    map = map[type];
    if(capture in map) {
      map = map[capture];
      var objHashCode = goog.getHashCode(obj);
      if(map[objHashCode]) {
        return map[objHashCode]
      }
    }
  }return null
};
goog.events.getListener = function(src, type, listener, opt_capt, opt_handler) {
  var capture = !(!opt_capt), listenerArray = goog.events.getListeners_(src, type, capture);
  if(listenerArray) {
    for(var i = 0;i < listenerArray.length;i++) {
      if(listenerArray[i].listener == listener && listenerArray[i].capture == capture && listenerArray[i].handler == opt_handler) {
        return listenerArray[i]
      }
    }
  }return null
};
goog.events.hasListener = function(obj, type, capture) {
  var map = goog.events.listenerTree_;
  if(type in map) {
    map = map[type];
    if(capture in map) {
      map = map[capture];
      var objHashCode = goog.getHashCode(obj);
      if(map[objHashCode]) {
        return true
      }
    }
  }return false
};
goog.events.expose = function(e) {
  var str = [];
  for(var key in e) {
    if(e[key] && e[key].id) {
      str.push(key + " = " + e[key] + " (" + e[key].id + ")")
    }else {
      str.push(key + " = " + e[key])
    }
  }return str.join("\n")
};
goog.events.EventType = {CLICK:"click", DBLCLICK:"dblclick", MOUSEDOWN:"mousedown", MOUSEUP:"mouseup", MOUSEOVER:"mouseover", MOUSEOUT:"mouseout", MOUSEMOVE:"mousemove", KEYPRESS:"keypress", KEYDOWN:"keydown", KEYUP:"keyup", BLUR:"blur", FOCUS:"focus", DEACTIVATE:"deactivate", FOCUSIN:goog.userAgent.IE ? "focusin" : "DOMFocusIn", FOCUSOUT:goog.userAgent.IE ? "focusout" : "DOMFocusOut", CHANGE:"change", SELECT:"select", SUBMIT:"submit", LOAD:"load", UNLOAD:"unload", HELP:"help", RESIZE:"resize", SCROLL:"scroll", 
READYSTATECHANGE:"readystatechange", CONTEXTMENU:"contextmenu"};
goog.events.getOnString_ = function(type) {
  if(type in goog.events.onStringMap_) {
    return goog.events.onStringMap_[type]
  }return goog.events.onStringMap_[type] = goog.events.onString_ + type
};
goog.events.fireListeners = function(obj, type, capture, eventObject) {
  var retval = 1, map = goog.events.listenerTree_;
  if(type in map) {
    map = map[type];
    if(capture in map) {
      map = map[capture];
      var objHashCode = goog.getHashCode(obj);
      if(map[objHashCode]) {
        var listenerArray = map[objHashCode];
        if(!listenerArray.locked_) {
          listenerArray.locked_ = 1
        }else {
          listenerArray.locked_++
        }try {
          var length = listenerArray.length;
          for(var i = 0;i < length;i++) {
            var listener = listenerArray[i];
            if(listener && !listener.removed) {
              retval &= goog.events.fireListener(listener, eventObject) !== false;
              if(listener.callOnce) {
                goog.events.unlistenByKey(listener.key)
              }
            }
          }
        }finally {
          listenerArray.locked_--;
          goog.events.cleanUp_(type, capture, objHashCode, listenerArray)
        }
      }
    }
  }return Boolean(retval)
};
goog.events.fireListener = function(listener, eventObject) {
  return listener.handleEvent(eventObject)
};
goog.events.getTotalListenerCount = function() {
  return goog.object.getCount(goog.events.listeners_)
};
goog.events.dispatchEvent = function(src, e) {
  if(goog.isString(e)) {
    e = new goog.events.Event(e, src)
  }else if(!(e instanceof goog.events.Event)) {
    var oldEvent = e;
    e = new goog.events.Event(e.type, src);
    goog.object.extend(e, oldEvent)
  }else {
    e.target = e.target || src
  }var rv = 1, ancestors, type = e.type, map = goog.events.listenerTree_;
  if(!(type in map)) {
    return true
  }map = map[type];
  var hasCapture = true in map, hasBubble = false in map;
  if(hasCapture) {
    ancestors = [];
    for(var parent = src;parent;parent = parent.getParentEventTarget()) {
      ancestors.push(parent)
    }for(var i = ancestors.length - 1;!e.propagationStopped_ && i >= 0;i--) {
      e.currentTarget = ancestors[i];
      rv &= goog.events.fireListeners(ancestors[i], e.type, true, e) && e.returnValue_ != false
    }
  }if(hasBubble) {
    if(hasCapture) {
      for(var i = 0;!e.propagationStopped_ && i < ancestors.length;i++) {
        e.currentTarget = ancestors[i];
        rv &= goog.events.fireListeners(ancestors[i], e.type, false, e) && e.returnValue_ != false
      }
    }else {
      for(var current = src;!e.propagationStopped_ && current;current = current.getParentEventTarget()) {
        e.currentTarget = current;
        rv &= goog.events.fireListeners(current, e.type, false, e) && e.returnValue_ != false
      }
    }
  }return Boolean(rv)
};
goog.events.handleBrowserEvent_ = function(key, opt_evt) {
  if(!goog.events.listeners_[key]) {
    return true
  }var listener = goog.events.listeners_[key], type = listener.type, map = goog.events.listenerTree_;
  if(!(type in map)) {
    return true
  }map = map[type];
  var retval;
  if(goog.userAgent.IE) {
    var ieEvent = opt_evt || window.event, hasCapture = true in map;
    if(hasCapture) {
      if(goog.events.isMarkedIeEvent_(ieEvent)) {
        return true
      }goog.events.markIeEvent_(ieEvent)
    }var srcHashCode = goog.getHashCode(listener.src), evt = goog.events.eventPool_.getObject();
    evt.init(ieEvent, this);
    retval = true;
    try {
      if(hasCapture) {
        var ancestors = goog.events.arrayPool_.getObject();
        for(var parent = evt.currentTarget;parent;parent = parent.parentNode) {
          ancestors.push(parent)
        }for(var i = ancestors.length - 1;!evt.propagationStopped_ && i >= 0;i--) {
          evt.currentTarget = ancestors[i];
          retval &= goog.events.fireListeners(ancestors[i], type, true, evt)
        }for(var i = 0;!evt.propagationStopped_ && i < ancestors.length;i++) {
          evt.currentTarget = ancestors[i];
          retval &= goog.events.fireListeners(ancestors[i], type, false, evt)
        }
      }else {
        retval = goog.events.fireListener(listener, evt)
      }
    }finally {
      if(ancestors) {
        ancestors.length = 0;
        goog.events.arrayPool_.releaseObject(ancestors)
      }evt.dispose();
      goog.events.eventPool_.releaseObject(evt)
    }return retval
  }var be = new goog.events.BrowserEvent(opt_evt, this);
  try {
    retval = goog.events.fireListener(listener, be)
  }finally {
    be.dispose()
  }return retval
};
goog.events.markIeEvent_ = function(e) {
  var useReturnValue = false;
  if(e.keyCode == 0) {
    try {
      e.keyCode = -1;
      return
    }catch(ex) {
      useReturnValue = true
    }
  }if(useReturnValue || e.returnValue == undefined) {
    e.returnValue = true
  }
};
goog.events.isMarkedIeEvent_ = function(e) {
  return e.keyCode < 0 || e.returnValue != undefined
};goog.events.EventTarget = function() {
};
goog.events.EventTarget.inherits(goog.Disposable);
goog.events.EventTarget.prototype.getParentEventTarget = function() {
  return null
};
goog.events.EventTarget.prototype.addEventListener = function(type, handler, opt_capture, opt_handlerScope) {
  goog.events.listen(this, type, handler, opt_capture, opt_handlerScope)
};
goog.events.EventTarget.prototype.removeEventListener = function(type, handler, opt_capture, opt_handlerScope) {
  goog.events.unlisten(this, type, handler, opt_capture, opt_handlerScope)
};
goog.events.EventTarget.prototype.dispatchEvent = function(e) {
  return goog.events.dispatchEvent(this, e)
};
goog.events.EventTarget.prototype.dispose = function() {
  if(!this.getDisposed()) {
    goog.Disposable.prototype.dispose.call(this);
    goog.events.removeAll(this)
  }
};
goog.events.EventTarget.prototype.customEvent_ = true;goog.net = {};
goog.net.EventType = {COMPLETE:"complete", SUCCESS:"success", ERROR:"error", ABORT:"abort", READY:"ready", READY_STATE_CHANGE:"readystatechange", TIMEOUT:"timeout"};goog.json = {};
goog.json.isValid_ = function(s) {
  if(s == "") {
    return false
  }s = s.replace(/"(\\.|[^"\\])*"/g, "");
  return s == "" || !/[^,:{}\[\]0-9.\-+Eaeflnr-u \n\r\t]/.test(s)
};
goog.json.parse = function(s) {
  s = String(s);
  if(typeof s.parseJSON == "function") {
    return s.parseJSON()
  }if(goog.json.isValid_(s)) {
    try {
      return eval("(" + s + ")")
    }catch(ex) {
    }
  }throw Error("Invalid JSON string: " + s);
};
goog.json.unsafeParse = function(s) {
  return eval("(" + s + ")")
};
goog.json.serializer_ = null;
goog.json.serialize = function(object) {
  if(!goog.json.serializer_) {
    goog.json.serializer_ = new goog.json.Serializer
  }return goog.json.serializer_.serialize(object)
};
goog.json.Serializer = function() {
};
goog.json.Serializer.prototype.serialize = function(object) {
  if(object != null && typeof object.toJSONString == "function") {
    return object.toJSONString()
  }var sb = [];
  this.serialize_(object, sb);
  return sb.join("")
};
goog.json.Serializer.prototype.serialize_ = function(object, sb) {
  switch(typeof object) {
    case "string":this.serializeString_(object, sb);
    break;
    case "number":this.serializeNumber_(object, sb);
    break;
    case "boolean":sb.push(object);
    break;
    case "undefined":sb.push("null");
    break;
    case "object":if(object == null) {
      sb.push("null");
      break
    }if(goog.isArray(object)) {
      this.serializeArray_(object, sb);
      break
    }this.serializeObject_(object, sb);
    break;
    default:throw Error("Unknown type: " + typeof object);
  }
};
goog.json.Serializer.charToJsonCharCache_ = {'"':'\\"', "\\":"\\\\", "/":"\\/", "\u0008":"\\b", "\u000c":"\\f", "\n":"\\n", "\r":"\\r", "\t":"\\t", "\u000b":"\\u000b"};
goog.json.Serializer.prototype.serializeString_ = function(s, sb) {
  sb.push('"', s.replace(/[\\\"\x00-\x1f\x80-\uffff]/g, function(c) {
    if(c in goog.json.Serializer.charToJsonCharCache_) {
      return goog.json.Serializer.charToJsonCharCache_[c]
    }var cc = c.charCodeAt(0), rv = "\\u";
    if(cc < 16) {
      rv += "000"
    }else if(cc < 256) {
      rv += "00"
    }else if(cc < 4096) {
      rv += "0"
    }return goog.json.Serializer.charToJsonCharCache_[c] = rv + cc.toString(16)
  }), '"')
};
goog.json.Serializer.prototype.serializeNumber_ = function(n, sb) {
  sb.push(isFinite(n) && !isNaN(n) ? n : "null")
};
goog.json.Serializer.prototype.serializeArray_ = function(arr, sb) {
  var l = arr.length;
  sb.push("[");
  var sep = "";
  for(var i = 0;i < l;i++) {
    sb.push(sep);
    this.serialize_(arr[i], sb);
    sep = ","
  }sb.push("]")
};
goog.json.Serializer.prototype.serializeObject_ = function(obj, sb) {
  sb.push("{");
  var sep = "";
  for(var key in obj) {
    sb.push(sep);
    this.serializeString_(key, sb);
    sb.push(":");
    this.serialize_(obj[key], sb);
    sep = ","
  }sb.push("}")
};goog.Uri = function(opt_uri, opt_ignoreCase) {
  var m;
  if(opt_uri instanceof goog.Uri) {
    this.setIgnoreCase(opt_ignoreCase == null ? opt_uri.getIgnoreCase() : opt_ignoreCase);
    this.setScheme(opt_uri.getScheme());
    this.setUserInfo(opt_uri.getUserInfo());
    this.setDomain(opt_uri.getDomain());
    this.setPort(opt_uri.getPort());
    this.setPath(opt_uri.getPath());
    this.setQueryData(opt_uri.getQueryData().clone());
    this.setFragment(opt_uri.getFragment())
  }else if(opt_uri && (m = String(opt_uri).match(goog.Uri.getRE_()))) {
    this.setIgnoreCase(!(!opt_ignoreCase));
    this.setScheme(m[1], true);
    this.setUserInfo(m[2], true);
    this.setDomain(m[3], true);
    this.setPort(m[4]);
    this.setPath(m[5], true);
    this.setQueryData(m[6]);
    this.setFragment(m[7], true)
  }else {
    this.setIgnoreCase(!(!opt_ignoreCase));
    this.queryData_ = new goog.Uri.QueryData(null, this, this.ignoreCase_)
  }
};
goog.Uri.RANDOM_PARAM = "zx";
goog.Uri.prototype.scheme_ = "";
goog.Uri.prototype.userInfo_ = "";
goog.Uri.prototype.domain_ = "";
goog.Uri.prototype.port_ = null;
goog.Uri.prototype.path_ = "";
goog.Uri.prototype.queryData_ = null;
goog.Uri.prototype.fragment_ = "";
goog.Uri.prototype.isReadOnly_ = false;
goog.Uri.prototype.ignoreCase_ = false;
goog.Uri.prototype.toString = function() {
  if(this.cachedToString_) {
    return this.cachedToString_
  }var out = [];
  if(this.scheme_) {
    out.push(goog.Uri.encodeSpecialChars_(this.scheme_, goog.Uri.reDisallowedInSchemeOrUserInfo_), ":")
  }if(this.domain_) {
    out.push("//");
    if(this.userInfo_) {
      out.push(goog.Uri.encodeSpecialChars_(this.userInfo_, goog.Uri.reDisallowedInSchemeOrUserInfo_), "@")
    }out.push(goog.Uri.encodeString_(this.domain_));
    if(this.port_ != null) {
      out.push(":", String(this.getPort()))
    }
  }if(this.path_) {
    out.push(goog.Uri.encodeSpecialChars_(this.path_, goog.Uri.reDisallowedInPath_))
  }var query = String(this.queryData_);
  if(query) {
    out.push("?", query)
  }if(this.fragment_) {
    out.push("#", goog.Uri.encodeString_(this.fragment_))
  }return this.cachedToString_ = out.join("")
};
goog.Uri.prototype.resolve = function(relativeUri) {
  var absoluteUri = this.clone(), overridden = relativeUri.hasScheme();
  if(overridden) {
    absoluteUri.setScheme(relativeUri.getScheme())
  }else {
    overridden = relativeUri.hasUserInfo()
  }if(overridden) {
    absoluteUri.setUserInfo(relativeUri.getUserInfo())
  }else {
    overridden = relativeUri.hasDomain()
  }if(overridden) {
    absoluteUri.setDomain(relativeUri.getDomain())
  }else {
    overridden = relativeUri.hasPort()
  }var path = relativeUri.getPath();
  if(overridden) {
    absoluteUri.setPort(relativeUri.getPort())
  }else {
    overridden = relativeUri.hasPath();
    if(overridden) {
      if(!/^\//.test(path)) {
        path = absoluteUri.getPath().replace(/\/?[^\/]*$/, "/" + path)
      }
    }
  }if(overridden) {
    absoluteUri.setPath(path)
  }else {
    overridden = relativeUri.hasQuery()
  }if(overridden) {
    absoluteUri.setQueryData(relativeUri.getQuery())
  }else {
    overridden = relativeUri.hasFragment()
  }if(overridden) {
    absoluteUri.setFragment(relativeUri.getFragment())
  }return absoluteUri
};
goog.Uri.prototype.clone = function() {
  return new goog.Uri.create(this.scheme_, this.userInfo_, this.domain_, this.port_, this.path_, this.queryData_.clone(), this.fragment_, this.ignoreCase_)
};
goog.Uri.prototype.getScheme = function() {
  return this.scheme_
};
goog.Uri.prototype.setScheme = function(newScheme, opt_decode) {
  this.enforceReadOnly_();
  delete this.cachedToString_;
  this.scheme_ = opt_decode ? goog.Uri.decodeOrEmpty_(newScheme) : newScheme;
  if(this.scheme_) {
    this.scheme_ = this.scheme_.replace(/:$/, "")
  }return this
};
goog.Uri.prototype.hasScheme = function() {
  return!(!this.scheme_)
};
goog.Uri.prototype.getUserInfo = function() {
  return this.userInfo_
};
goog.Uri.prototype.setUserInfo = function(newUserInfo, opt_decode) {
  this.enforceReadOnly_();
  delete this.cachedToString_;
  this.userInfo_ = opt_decode ? goog.Uri.decodeOrEmpty_(newUserInfo) : newUserInfo;
  return this
};
goog.Uri.prototype.hasUserInfo = function() {
  return!(!this.userInfo_)
};
goog.Uri.prototype.getDomain = function() {
  return this.domain_
};
goog.Uri.prototype.setDomain = function(newDomain, opt_decode) {
  this.enforceReadOnly_();
  delete this.cachedToString_;
  this.domain_ = opt_decode ? goog.Uri.decodeOrEmpty_(newDomain) : newDomain;
  return this
};
goog.Uri.prototype.hasDomain = function() {
  return!(!this.domain_)
};
goog.Uri.prototype.getPort = function() {
  return this.port_
};
goog.Uri.prototype.setPort = function(newPort) {
  this.enforceReadOnly_();
  delete this.cachedToString_;
  if(newPort) {
    newPort = Number(newPort);
    if(isNaN(newPort) || newPort < 0) {
      throw Error("Bad port number " + newPort);
    }this.port_ = newPort
  }else {
    this.port_ = null
  }return this
};
goog.Uri.prototype.hasPort = function() {
  return this.port_ != null
};
goog.Uri.prototype.getPath = function() {
  return this.path_
};
goog.Uri.prototype.setPath = function(newPath, opt_decode) {
  this.enforceReadOnly_();
  delete this.cachedToString_;
  this.path_ = opt_decode ? goog.Uri.decodeOrEmpty_(newPath) : newPath;
  return this
};
goog.Uri.prototype.hasPath = function() {
  return!(!this.path_)
};
goog.Uri.prototype.hasQuery = function() {
  return this.queryData_ !== null && this.queryData_.toString() !== ""
};
goog.Uri.prototype.setQueryData = function(queryData) {
  this.enforceReadOnly_();
  delete this.cachedToString_;
  if(queryData instanceof goog.Uri.QueryData) {
    this.queryData_ = queryData;
    this.queryData_.uri_ = this;
    this.queryData_.setIgnoreCase(this.ignoreCase_)
  }else {
    this.queryData_ = new goog.Uri.QueryData(queryData, this, this.ignoreCase_)
  }return this
};
goog.Uri.prototype.getQuery = function() {
  return this.queryData_.toString()
};
goog.Uri.prototype.getQueryData = function() {
  return this.queryData_
};
goog.Uri.prototype.getFragment = function() {
  return this.fragment_
};
goog.Uri.prototype.setFragment = function(newFragment, opt_decode) {
  this.enforceReadOnly_();
  delete this.cachedToString_;
  this.fragment_ = opt_decode ? goog.Uri.decodeOrEmpty_(newFragment) : newFragment;
  return this
};
goog.Uri.prototype.hasFragment = function() {
  return!(!this.fragment_)
};
goog.Uri.prototype.hasSameDomainAs = function(uri2) {
  return(!this.hasDomain() && !uri2.hasDomain() || this.getDomain() == uri2.getDomain()) && (!this.hasPort() && !uri2.hasPort() || this.getPort() == uri2.getPort())
};
goog.Uri.prototype.enforceReadOnly_ = function() {
  if(this.isReadOnly_) {
    throw Error("Tried to modify a read-only Uri");
  }
};
goog.Uri.prototype.setIgnoreCase = function(ignoreCase) {
  this.ignoreCase_ = ignoreCase;
  if(this.queryData_) {
    this.queryData_.setIgnoreCase(ignoreCase)
  }
};
goog.Uri.prototype.getIgnoreCase = function() {
  return this.ignoreCase_
};
goog.Uri.parse = function(uri, opt_ignoreCase) {
  return uri instanceof goog.Uri ? uri.clone() : new goog.Uri(uri, opt_ignoreCase)
};
goog.Uri.create = function(opt_scheme, opt_userInfo, opt_domain, opt_port, opt_path, opt_query, opt_fragment, opt_ignoreCase) {
  var uri = new goog.Uri(null, opt_ignoreCase);
  uri.setScheme(opt_scheme);
  uri.setUserInfo(opt_userInfo);
  uri.setDomain(opt_domain);
  uri.setPort(opt_port);
  uri.setPath(opt_path);
  uri.setQueryData(opt_query);
  uri.setFragment(opt_fragment);
  return uri
};
goog.Uri.resolve = function(base, rel) {
  if(!(base instanceof goog.Uri)) {
    base = goog.Uri.parse(base)
  }if(!(rel instanceof goog.Uri)) {
    rel = goog.Uri.parse(rel)
  }return base.resolve(rel)
};
goog.Uri.decodeOrEmpty_ = function(val) {
  return val ? goog.string.urlDecode(val) : ""
};
goog.Uri.encodeString_ = function(unescapedPart) {
  if(goog.isString(unescapedPart)) {
    return encodeURIComponent(unescapedPart)
  }return null
};
goog.Uri.encodeSpecialRegExp_ = /^[a-zA-Z0-9\-_.!~*'():\/;?]*$/;
goog.Uri.encodeSpecialChars_ = function(unescapedPart, extra) {
  var ret = null;
  if(goog.isString(unescapedPart)) {
    ret = unescapedPart;
    if(!goog.Uri.encodeSpecialRegExp_.test(ret)) {
      ret = encodeURI(unescapedPart)
    }if(ret.search(extra) >= 0) {
      ret = ret.replace(extra, goog.Uri.encodeChar_)
    }
  }return ret
};
goog.Uri.encodeChar_ = function(ch) {
  var n = ch.charCodeAt(0);
  return"%" + (n >> 4 & 15).toString(16) + (n & 15).toString(16)
};
goog.Uri.re_ = null;
goog.Uri.getRE_ = function() {
  if(!goog.Uri.re_) {
    goog.Uri.re_ = /^(?:([^:\/?#]+):)?(?:\/\/(?:([^\/?#]*)@)?([^\/?#:@]*)(?::([0-9]+))?)?([^?#]+)?(?:\?([^#]*))?(?:#(.*))?$/
  }return goog.Uri.re_
};
goog.Uri.reDisallowedInSchemeOrUserInfo_ = /[#\/\?@]/g;
goog.Uri.reDisallowedInPath_ = /[\#\?]/g;
goog.Uri.haveSameDomain = function(uri1String, uri2String) {
  var uri1 = new goog.Uri(uri1String), uri2 = new goog.Uri(uri2String);
  return uri1.hasSameDomainAs(uri2)
};
goog.Uri.QueryData = function(opt_query, opt_uri, opt_ignoreCase) {
  this.keyMap_ = new goog.structs.Map;
  this.uri_ = opt_uri;
  this.ignoreCase_ = !(!opt_ignoreCase);
  if(opt_query) {
    var pairs = opt_query.split("&");
    for(var i = 0;i < pairs.length;i++) {
      var parts = pairs[i].split("="), name = goog.string.urlDecode(parts[0]);
      name = this.getKeyName_(name);
      this.add(name, parts.length > 1 ? goog.string.urlDecode(parts[1]) : "")
    }
  }
};
goog.Uri.QueryData.prototype.count_ = 0;
goog.Uri.QueryData.prototype.getCount = function() {
  return this.count_
};
goog.Uri.QueryData.prototype.add = function(key, value) {
  this.invalidateCache_();
  key = this.getKeyName_(key);
  if(!this.containsKey(key)) {
    this.keyMap_.set(key, value)
  }else {
    var current = this.keyMap_.get(key);
    if(goog.isArray(current)) {
      current.push(value)
    }else {
      this.keyMap_.set(key, [current, value])
    }
  }this.count_++;
  return this
};
goog.Uri.QueryData.prototype.remove = function(key) {
  key = this.getKeyName_(key);
  if(this.keyMap_.containsKey(key)) {
    this.invalidateCache_();
    var old = this.keyMap_.get(key);
    if(goog.isArray(old)) {
      this.count_ -= old.length
    }else {
      this.count_--
    }return this.keyMap_.remove(key)
  }return false
};
goog.Uri.QueryData.prototype.clear = function() {
  this.invalidateCache_();
  this.keyMap_.clear();
  this.count_ = 0
};
goog.Uri.QueryData.prototype.isEmpty = function() {
  return this.count_ == 0
};
goog.Uri.QueryData.prototype.containsKey = function(key) {
  key = this.getKeyName_(key);
  return this.keyMap_.containsKey(key)
};
goog.Uri.QueryData.prototype.containsValue = function(value) {
  var vals = this.getValues();
  return goog.array.contains(vals, value)
};
goog.Uri.QueryData.prototype.getKeys = function() {
  var vals = this.keyMap_.getValues(), keys = this.keyMap_.getKeys(), rv = [];
  for(var i = 0;i < keys.length;i++) {
    var val = vals[i];
    if(goog.isArray(val)) {
      for(var j = 0;j < val.length;j++) {
        rv.push(keys[i])
      }
    }else {
      rv.push(keys[i])
    }
  }return rv
};
goog.Uri.QueryData.prototype.getValues = function(opt_key) {
  var rv;
  if(opt_key) {
    var key = this.getKeyName_(opt_key);
    if(this.containsKey(key)) {
      var value = this.keyMap_.get(key);
      if(goog.isArray(value)) {
        return value
      }else {
        rv = [];
        rv.push(value)
      }
    }else {
      rv = []
    }
  }else {
    var vals = this.keyMap_.getValues();
    rv = [];
    for(var i = 0;i < vals.length;i++) {
      var val = vals[i];
      if(goog.isArray(val)) {
        goog.array.extend(rv, val)
      }else {
        rv.push(val)
      }
    }
  }return rv
};
goog.Uri.QueryData.prototype.set = function(key, value) {
  this.invalidateCache_();
  key = this.getKeyName_(key);
  if(this.containsKey(key)) {
    var old = this.keyMap_.get(key);
    if(goog.isArray(old)) {
      this.count_ -= old.length
    }else {
      this.count_--
    }
  }this.keyMap_.set(key, value);
  this.count_++;
  return this
};
goog.Uri.QueryData.prototype.get = function(key, opt_default) {
  key = this.getKeyName_(key);
  if(this.containsKey(key)) {
    var val = this.keyMap_.get(key);
    if(goog.isArray(val)) {
      return val[0]
    }else {
      return val
    }
  }else {
    return opt_default
  }
};
goog.Uri.QueryData.prototype.toString = function() {
  if(this.cachedToString_) {
    return this.cachedToString_
  }var sb = [], count = 0, keys = this.keyMap_.getKeys();
  for(var i = 0;i < keys.length;i++) {
    var key = keys[i], encodedKey = goog.string.urlEncode(key), val = this.keyMap_.get(key);
    if(goog.isArray(val)) {
      for(var j = 0;j < val.length;j++) {
        if(count > 0) {
          sb.push("&")
        }sb.push(encodedKey, "=", goog.string.urlEncode(val[j]));
        count++
      }
    }else {
      if(count > 0) {
        sb.push("&")
      }sb.push(encodedKey, "=", goog.string.urlEncode(val));
      count++
    }
  }return this.cachedToString_ = sb.join("")
};
goog.Uri.QueryData.prototype.invalidateCache_ = function() {
  delete this.cachedToString_;
  if(this.uri_) {
    delete this.uri_.cachedToString_
  }
};
goog.Uri.QueryData.prototype.clone = function() {
  var rv = new goog.Uri.QueryData;
  rv.keyMap_ = this.keyMap_.clone();
  return rv
};
goog.Uri.QueryData.prototype.getKeyName_ = function(arg) {
  var keyName = String(arg);
  if(this.ignoreCase_) {
    keyName = keyName.toLowerCase()
  }return keyName
};
goog.Uri.QueryData.prototype.setIgnoreCase = function(ignoreCase) {
  var resetKeys = ignoreCase && !this.ignoreCase_;
  if(resetKeys) {
    this.invalidateCache_();
    goog.structs.forEach(this.keyMap_, function(value, key, map) {
      var lowerCase = key.toLowerCase();
      if(key != lowerCase) {
        this.remove(key);
        this.add(lowerCase, value)
      }
    }, this)
  }this.ignoreCase_ = ignoreCase
};goog.net.CrossDomainRpc = function() {
  goog.events.EventTarget.call(this)
};
goog.net.CrossDomainRpc.inherits(goog.events.EventTarget);
goog.net.CrossDomainRpc.RESPONSE_MARKER_ = "xdrp";
goog.net.CrossDomainRpc.useFallBackDummyResource_ = true;
goog.net.CrossDomainRpc.isInResponseIframe_ = function() {
  return window.location && (window.location.hash.indexOf(goog.net.CrossDomainRpc.RESPONSE_MARKER_) == 1 || window.location.search.indexOf(goog.net.CrossDomainRpc.RESPONSE_MARKER_) == 1)
};
if(goog.net.CrossDomainRpc.isInResponseIframe_()) {
  if(goog.userAgent.IE) {
    document.execCommand("Stop")
  }else if(goog.userAgent.GECKO) {
    window.stop()
  }else {
    throw Error("stopped");
  }
}goog.net.CrossDomainRpc.setDummyResourceUri = function(dummyResourceUri) {
  goog.net.CrossDomainRpc.dummyResourceUri_ = dummyResourceUri
};
goog.net.CrossDomainRpc.setUseFallBackDummyResource = function(useFallBack) {
  goog.net.CrossDomainRpc.useFallBackDummyResource_ = useFallBack
};
goog.net.CrossDomainRpc.send = function(uri, opt_continuation, opt_method, opt_params, opt_headers) {
  var xdrpc = new goog.net.CrossDomainRpc;
  if(opt_continuation) {
    goog.events.listen(xdrpc, goog.net.EventType.COMPLETE, opt_continuation)
  }goog.events.listen(xdrpc, goog.net.EventType.READY, xdrpc.dispose);
  xdrpc.sendRequest(uri, opt_method, opt_params, opt_headers)
};
goog.net.CrossDomainRpc.setDebugMode = function(flag) {
  goog.net.CrossDomainRpc.debugMode_ = flag
};
goog.net.CrossDomainRpc.logger_ = goog.debug.Logger.getLogger("goog.net.CrossDomainRpc");
goog.net.CrossDomainRpc.createInputHtml_ = function(name, value) {
  return'<textarea name="' + name + '">' + value + "</textarea>"
};
goog.net.CrossDomainRpc.getDummyResourceUri_ = function() {
  if(goog.net.CrossDomainRpc.dummyResourceUri_) {
    return goog.net.CrossDomainRpc.dummyResourceUri_
  }if(goog.userAgent.GECKO) {
    var links = document.getElementsByTagName("link");
    for(var i = 0;i < links.length;i++) {
      var link = links[i];
      if(link.rel == "stylesheet" && goog.Uri.haveSameDomain(link.href, window.location.href)) {
        return link.href
      }
    }
  }var images = document.getElementsByTagName("img");
  for(var i = 0;i < images.length;i++) {
    var image = images[i];
    if(goog.Uri.haveSameDomain(image.src, window.location.href)) {
      return image.src
    }
  }if(!goog.net.CrossDomainRpc.useFallBackDummyResource_) {
    throw Error("No suitable dummy resource specified or detected for this page");
  }if(goog.userAgent.IE) {
    var pound = window.location.href.indexOf("#");
    if(pound > 0) {
      return window.location.href.substring(0, pound)
    }else {
      return window.location.href
    }
  }else {
    var locationHref = window.location.href, rootSlash = locationHref.indexOf("/", locationHref.indexOf("//") + 2), rootHref = locationHref.substring(0, rootSlash);
    return rootHref + "/robots.txt"
  }
};
goog.net.CrossDomainRpc.nextRequestId_ = 0;
goog.net.CrossDomainRpc.HEADER = "xdh:";
goog.net.CrossDomainRpc.PARAM = "xdp:";
goog.net.CrossDomainRpc.PARAM_ECHO = "xdpe:";
goog.net.CrossDomainRpc.PARAM_ECHO_REQUEST_ID = goog.net.CrossDomainRpc.PARAM_ECHO + "request-id";
goog.net.CrossDomainRpc.PARAM_ECHO_DUMMY_URI = goog.net.CrossDomainRpc.PARAM_ECHO + "dummy-uri";
goog.net.CrossDomainRpc.REQUEST_MARKER_ = "xdrq";
goog.net.CrossDomainRpc.prototype.sendRequest = function(uri, opt_method, opt_params, opt_headers) {
  var requestFrame = this.requestFrame_ = document.createElement("iframe"), requestId = goog.net.CrossDomainRpc.nextRequestId_++;
  requestFrame.id = goog.net.CrossDomainRpc.REQUEST_MARKER_ + "-" + requestId;
  if(!goog.net.CrossDomainRpc.debugMode_) {
    requestFrame.style.position = "absolute";
    requestFrame.style.top = "-5000px";
    requestFrame.style.left = "-5000px"
  }document.body.appendChild(requestFrame);
  var inputs = [];
  inputs.push(goog.net.CrossDomainRpc.createInputHtml_(goog.net.CrossDomainRpc.PARAM_ECHO_REQUEST_ID, requestId));
  var dummyUri = goog.net.CrossDomainRpc.getDummyResourceUri_();
  goog.net.CrossDomainRpc.logger_.log(goog.debug.Logger.Level.FINE, "dummyUri: " + dummyUri);
  inputs.push(goog.net.CrossDomainRpc.createInputHtml_(goog.net.CrossDomainRpc.PARAM_ECHO_DUMMY_URI, dummyUri));
  if(opt_params) {
    for(var name in opt_params) {
      var value = opt_params[name];
      inputs.push(goog.net.CrossDomainRpc.createInputHtml_(goog.net.CrossDomainRpc.PARAM + name, value))
    }
  }if(opt_headers) {
    for(var name in opt_headers) {
      var value = opt_headers[name];
      inputs.push(goog.net.CrossDomainRpc.createInputHtml_(goog.net.CrossDomainRpc.HEADER + name, value))
    }
  }var requestFrameContent = '<body><form method="' + (opt_method == "GET" ? "GET" : "POST") + '" action="' + uri + '">' + inputs.join("") + "</form></body>", requestFrameDoc = goog.dom.getFrameContentDocument(requestFrame);
  requestFrameDoc.open();
  requestFrameDoc.write(requestFrameContent);
  requestFrameDoc.close();
  requestFrameDoc.forms[0].submit();
  requestFrameDoc = null;
  this.loadListenerKey_ = goog.events.listen(requestFrame, goog.events.EventType.LOAD, function() {
    goog.net.CrossDomainRpc.logger_.log(goog.debug.Logger.Level.FINE, "response ready");
    this.responseReady_ = true
  }, false, this);
  this.receiveResponse_()
};
goog.net.CrossDomainRpc.RESPONSE_POLLING_PERIOD_ = 50;
goog.net.CrossDomainRpc.SEND_RESPONSE_TIME_OUT_ = 500;
goog.net.CrossDomainRpc.prototype.receiveResponse_ = function() {
  this.timeWaitedAfterResponseReady_ = 0;
  var responseDetectorHandle = window.setInterval(goog.bind(function() {
    this.detectResponse_(responseDetectorHandle)
  }, this), goog.net.CrossDomainRpc.RESPONSE_POLLING_PERIOD_)
};
goog.net.CrossDomainRpc.prototype.detectResponse_ = function(responseDetectorHandle) {
  var requestFrameWindow = this.requestFrame_.contentWindow, grandChildrenLength = requestFrameWindow.frames.length, responseInfoFrame = null;
  if(grandChildrenLength > 0 && goog.net.CrossDomainRpc.isResponseInfoFrame_(responseInfoFrame = requestFrameWindow.frames[grandChildrenLength - 1])) {
    goog.net.CrossDomainRpc.logger_.log(goog.debug.Logger.Level.FINE, "xd response ready");
    window.clearInterval(responseDetectorHandle);
    var responseInfoPayload = goog.net.CrossDomainRpc.getFramePayload_(responseInfoFrame).substring(1), params = new goog.Uri.QueryData(responseInfoPayload), chunks = [], numChunks = Number(params.get("n"));
    goog.net.CrossDomainRpc.logger_.log(goog.debug.Logger.Level.FINE, "xd response number of chunks: " + numChunks);
    for(var i = 0;i < numChunks;i++) {
      var responseFrame = requestFrameWindow.frames[i], responseChunkPayload = goog.net.CrossDomainRpc.getFramePayload_(responseFrame), chunkIndex = responseChunkPayload.indexOf(goog.net.CrossDomainRpc.PARAM_CHUNK_) + goog.net.CrossDomainRpc.PARAM_CHUNK_.length + 1, chunk = responseChunkPayload.substring(chunkIndex);
      chunks.push(chunk)
    }var responseData = chunks.join("");
    if(!goog.userAgent.IE) {
      responseData = decodeURIComponent(responseData)
    }this.status = Number(params.get("status"));
    this.responseText = responseData;
    this.responseTextIsJson_ = params.get("isDataJson") == "true";
    this.responseHeaders = goog.json.unsafeParse(params.get("headers"));
    this.dispatchEvent(goog.net.EventType.READY);
    this.dispatchEvent(goog.net.EventType.COMPLETE)
  }else {
    if(this.responseReady_) {
      this.timeWaitedAfterResponseReady_ += goog.net.CrossDomainRpc.RESPONSE_POLLING_PERIOD_;
      if(this.timeWaitedAfterResponseReady_ > goog.net.CrossDomainRpc.SEND_RESPONSE_TIME_OUT_) {
        goog.net.CrossDomainRpc.logger_.log(goog.debug.Logger.Level.FINE, "xd response timed out");
        window.clearInterval(responseDetectorHandle);
        this.status = 500;
        this.responseText = "response timed out";
        this.dispatchEvent(goog.net.EventType.READY);
        this.dispatchEvent(goog.net.EventType.ERROR);
        this.dispatchEvent(goog.net.EventType.COMPLETE)
      }
    }
  }
};
goog.net.CrossDomainRpc.isResponseInfoFrame_ = function(frame) {
  try {
    return goog.net.CrossDomainRpc.getFramePayload_(frame).indexOf(goog.net.CrossDomainRpc.RESPONSE_INFO_MARKER_) == 1
  }catch(e) {
    return false
  }
};
goog.net.CrossDomainRpc.getFramePayload_ = function(frame) {
  var href = frame.location.href, question = href.indexOf("?"), hash = href.indexOf("#"), delimiter = question < 0 ? hash : (hash < 0 ? question : Math.min(question, hash));
  return href.substring(delimiter)
};
goog.net.CrossDomainRpc.prototype.dispose = function() {
  if(!goog.net.CrossDomainRpc.debugMode_) {
    goog.net.CrossDomainRpc.logger_.log(goog.debug.Logger.Level.FINE, "request frame removed: " + this.requestFrame_.id);
    goog.events.unlistenByKey(this.loadListenerKey_);
    this.requestFrame_.parentNode.removeChild(this.requestFrame_)
  }delete this.requestFrame_
};
goog.net.CrossDomainRpc.RESPONSE_INFO_MARKER_ = goog.net.CrossDomainRpc.RESPONSE_MARKER_ + "-info";
goog.net.CrossDomainRpc.MAX_CHUNK_SIZE_ = goog.userAgent.IE ? 4095 : 1048576;
goog.net.CrossDomainRpc.PARAM_CHUNK_ = "chunk";
goog.net.CrossDomainRpc.CHUNK_PREFIX_ = goog.net.CrossDomainRpc.RESPONSE_MARKER_ + "=1&" + goog.net.CrossDomainRpc.PARAM_CHUNK_ + "=";
goog.net.CrossDomainRpc.sendResponse = function(data, isDataJson, echo, status, headers) {
  var dummyUri = echo[goog.net.CrossDomainRpc.PARAM_ECHO_DUMMY_URI], chunkSize = goog.net.CrossDomainRpc.MAX_CHUNK_SIZE_ - dummyUri.length - 1 - goog.net.CrossDomainRpc.CHUNK_PREFIX_.length - 1;
  if(!goog.userAgent.IE) {
    data = encodeURIComponent(data)
  }var numChunksToSend = Math.ceil(data.length / chunkSize);
  if(numChunksToSend == 0) {
    goog.net.CrossDomainRpc.createResponseInfo_(dummyUri, numChunksToSend, isDataJson, status, headers)
  }else {
    var numChunksSent = 0;
    function checkToCreateResponseInfo_() {
      if(++numChunksSent == numChunksToSend) {
        goog.net.CrossDomainRpc.createResponseInfo_(dummyUri, numChunksToSend, isDataJson, status, headers)
      }
    };
    for(var i = 0;i < numChunksToSend;i++) {
      var chunkStart = i * chunkSize, chunkEnd = chunkStart + chunkSize, chunk = chunkEnd > data.length ? data.substring(chunkStart) : data.substring(chunkStart, chunkEnd), responseFrame = document.createElement("iframe");
      responseFrame.src = dummyUri + goog.net.CrossDomainRpc.getPayloadDelimiter_(dummyUri) + goog.net.CrossDomainRpc.CHUNK_PREFIX_ + chunk;
      document.body.appendChild(responseFrame);
      checkToCreateResponseInfo_()
    }
  }
};
goog.net.CrossDomainRpc.createResponseInfo_ = function(dummyUri, numChunks, isDataJson, status, headers) {
  var responseInfoFrame = document.createElement("iframe");
  document.body.appendChild(responseInfoFrame);
  responseInfoFrame.src = dummyUri + goog.net.CrossDomainRpc.getPayloadDelimiter_(dummyUri) + goog.net.CrossDomainRpc.RESPONSE_INFO_MARKER_ + "=1&n=" + numChunks + "&isDataJson=" + isDataJson + "&status=" + status + "&headers=" + encodeURIComponent(headers)
};
goog.net.CrossDomainRpc.getPayloadDelimiter_ = function(dummyUri) {
  return goog.net.CrossDomainRpc.REFERRER_ == dummyUri ? "?" : "#"
};
goog.net.CrossDomainRpc.removeUriParams_ = function(uri) {
  var question = uri.indexOf("?");
  if(question > 0) {
    uri = uri.substring(0, question)
  }var hash = uri.indexOf("#");
  if(hash > 0) {
    uri = uri.substring(0, hash)
  }return uri
};
goog.net.CrossDomainRpc.REFERRER_ = goog.net.CrossDomainRpc.removeUriParams_(document.referrer);xdSendRequest = goog.net.CrossDomainRpc.send;
xdSendResponse = goog.net.CrossDomainRpc.sendResponse;
