// -----
// utils

function $(id) {
  return document.getElementById(id);
};

function show(id, show) {
  var element = $(id);
  if (element) {
    element.style.display = show == undefined ? '' : show == false ? 'none' : '';
  }
};

function hide(id) {
  show(id, false);
};

function enable(id, show) {
  var element = $(id);
  if (element) {
    element.disabled = show == undefined ? false : show == false ? true : false;
  }
};

function disable(id) {
  enable(id, false);
};

function copyProperties(from, to) {
  for (var p in from) {
    to[p] = from[p];
  }
};

function trim(str) {
  return str ? str.replace(/^[\s\xa0]+|[\s\xa0]+$/g, '') : str;
};

function showElementBusy(id, busy) {
  $(id).innerHTML = busy || busy == undefined ? SPINNER : '';
};

function noCache(url) {
  return url + '?nocache=' + (new Date().getTime());
};

// ---------
// constants

var SERVICE_NAME = 'esp';
var APP_NAME = 'domain-gadget-directory-manager';

var STATE_PENDING = 'pending';
var STATE_OK = 'ok';
var STATE_ERROR = 'error';

var MAX_LONG = 9223372036854776000;

var MAX_NO_SCROLL_ROWS = 20;
var MAX_SCROLLBAR_HEIGHT = 360;

var SPINNER = '<img src="http://google-feedserver.googlecode.com/svn/trunk/resources/gadgets/domain-gadget-directory-manager/spinner.gif">';

var NO_FILTER = 'no-filter';
var WHITE_LIST_FILTER = 'white-list-filter';
var BLACK_LIST_FILTER = 'black-list-filter';

var EMPTY_RESPONSE = 'empty response';

// ----------------
// global variables

var privateGadgets = null;

var privateGadgetSpecFeedUrl = null;
var privateGadgetSpecFeedLoaded = false;

var privateGadgetFeedUrl = null;
var privateGadgetFeedLoaded = false;

var publishedGadgetUrls = {};

var privateGadgetCategoryFeedUrl = null;
var privateGadgetCategoryFeedLoaded = false;
var selectedPrivateGadgetCategories = {};

var publicGadgetFeedUrl = null;
var publicGadgets = [];

var domainPublicGadgetFeedUrl = null;
var domainPublicGadgets = [];

var domainPrivateGadgetFeedUrl = null;
var domainPrivateGadgets = [];

var domainFilterListedGadgets = {};
domainFilterListedGadgets[WHITE_LIST_FILTER] = [];
domainFilterListedGadgets[BLACK_LIST_FILTER] = [];

var domainFilterListedGadgetFeedUrl = {};
domainFilterListedGadgetFeedUrl[WHITE_LIST_FILTER] = null;
domainFilterListedGadgetFeedUrl[BLACK_LIST_FILTER] = null;

// -----------------
// private directory

function initDirectoryManager() {
  if (privateGadgetSpecFeedUrl && !privateGadgetSpecFeedLoaded) {
    privateGadgetSpecFeedLoaded = true;
    $('gadget-list').innerHTML = SPINNER;
    loadPrivateGadgetList();
  }

  if (privateGadgetFeedUrl && !privateGadgetFeedLoaded) {
    privateGadgetFeedLoaded = true;
    $('gadget-list').innerHTML = SPINNER;
    loadPublishedGadgetList();
  }

  if (privateGadgetCategoryFeedUrl && !privateGadgetCategoryFeedLoaded) {
    privateGadgetCategoryFeedLoaded = true;
    $('category-list').innerHTML = SPINNER;
    loadGadgetCategoryList();
  }
};

//// google.load('gdata', '1.x', {packages: ['core']});
// google.load('gdata', '1.x', {packages: ['core'], 'other_params': 'debug=1'});
// google.setOnLoadCallback(initDirectoryManager);

function loadPrivateGadgetList() {
  loadGadgetList(noCache(privateGadgetSpecFeedUrl), function(response) {
    privateGadgets = response && response.feed.entry ? response.feed.entry : [];
    showPrivateGadgets();
  });
};

function loadPublishedGadgetList() {
  loadGadgetList(privateGadgetFeedUrl, function(response) {
    if (response) {
      var entries = response.feed.entry || [];
      for (var i = 0; i < entries.length; i++) {
        var gadgetSpecUrl = entries[i].content.entity.url;
        var gadgetName = gadgetSpecUrl.substring(gadgetSpecUrl.lastIndexOf('/') + 1);
        publishedGadgetUrls[gadgetName] = entries[i].id.$t;
      }
      showPrivateGadgets();
    }
  });
};

function createService() {
  var service = new google.gdata.client.FeedServerService(SERVICE_NAME, APP_NAME);
  service.setGadgetsAuthentication('SIGNED');
  return service;
};

function loadGadgetList(url, continuation) {
  var service = createService();
  var params = {};
  params[gadgets.io.RequestParameters.REFRESH_INTERVAL] = 0;  // no caching
  service.setGadgetsAuthentication('SIGNED', params);
  service.getFeed(url, continuation, showMessage);
};

function bold(s, cond) {
  return cond ? '<b>' + s + '</b>' : s;
};

function getPublishUnpublishButton(entity, i, published) {
  switch(entity.$state) {
    case STATE_PENDING:
      return '<div class="right" style="margin:0px">' + SPINNER + '</div>';

    case STATE_ERROR:
    default:
      return '<a class="right" href="javascript:void(0)" onclick="' +
          (published ? 'unpublishGadget' : 'publishGadget') + '(' + i + ')">' +
          bold((published ? 'Unpublish' : 'Publish'), published) + '</a>';
  }
};

function showPrivateGadgets() {
  if (privateGadgets) {
    var length = privateGadgets.length;
    if (length > MAX_NO_SCROLL_ROWS) {
      var style = $('gadget-list').style;
      style.height = MAX_SCROLLBAR_HEIGHT + 'px';
    }

    var html = [];
    for (var i = 0; i < length; i++) {
      var gadget = privateGadgets[i];
      var entity = gadget.content.entity;
      var name = entity.name;
      var published = publishedGadgetUrls[name];
      html.push('<div class="list-item right-container" title="', name, '">',
          bold(name + (entity.$state == STATE_ERROR ?
              ' (<span title="' + entity.$stateInfo + '">ERROR</span>)' : ''), published),
          getPublishUnpublishButton(entity, i, published), '</div>');
    }
    $('gadget-list').innerHTML = html.join('');
    gadgets.window.adjustHeight();
  }
};

function publishGadget(i) {
  var entry = privateGadgets[i];
  var specUrl = entry.id.$t;
  var entity = entry.content.entity;
  var name = entity.name;

  var entry = {xmlns: 'http://www.w3.org/2005/Atom', content: {
      type: 'application/xml', entity: {url: specUrl}}};
  var service = createService();
  service.insertEntry(privateGadgetFeedUrl, entry, function(response) {
    if (response) {
      entity.$state = STATE_OK;
      publishedGadgetUrls[name] = response.entry.id.$t;
    } else {
      entity.$state = STATE_ERROR;
      entity.$stateInfo = EMPTY_RESPONSE;
    }

    showPrivateGadgets();
  }, function(error) {
    entity.$state = STATE_ERROR;
    entity.$stateInfo = error;
    showPrivateGadgets();
  });

  entity.$state = STATE_PENDING;
  showPrivateGadgets();
};

function unpublishGadget(i) {
  var entity = privateGadgets[i].content.entity;
  var name = entity.name;

  var service = createService();
  var directoryEntryId = publishedGadgetUrls[name];
  service.deleteEntry(directoryEntryId, function(response) {
    entity.$state = STATE_OK;
    delete publishedGadgetUrls[name];
    showPrivateGadgets();
  }, function(error) {
    entity.$state = STATE_ERROR;
    entity.$stateInfo = error;
    showPrivateGadgets();
  });

  entity.$state = STATE_PENDING;
  showPrivateGadgets();
};

// ------------------
// private categories

function loadGadgetCategoryList() {
  loadGadgetList(privateGadgetCategoryFeedUrl, function(response) {
    if (response) {
      privateGadgetCategories = response.feed.entry || [];
      showPrivateGadgetCategories();
    }
  });
};

function getRemoveCategoryButton(entity, i) {
  switch(entity.$state) {
    case STATE_PENDING:
      return '<div class="right">' + SPINNER + '</div>';

    case STATE_ERROR:
      return 'ERROR';

    default:
      return '<a class="right" href="javascript:void(0)" onclick="removeCategory(' + i +
          ')">Remove</a>';
  }
};

function showPrivateGadgetCategories() {
  if (privateGadgetCategories) {
    var length = privateGadgetCategories.length;
    if (length > MAX_NO_SCROLL_ROWS) {
      var style = $('category-list').style;
      style.height = MAX_SCROLLBAR_HEIGHT + 'px';
    }

    var html = [];
    for (var i = 0; i < length; i++) {
      var entity = privateGadgetCategories[i].content.entity;
      // var category = entity.category.length ? entity.category[0] : entity.category;
      // var displayName = category.displayName;
      var displayName = entity.name;
      html.push('<div class="list-item right-container" title="', displayName,
          '">', displayName, getRemoveCategoryButton(entity, i), '</div>');
    }
    $('category-list').innerHTML = html.join('');
    gadgets.window.adjustHeight();
  }
};

function removeCategory(i) {
  var entry = privateGadgetCategories[i];
  var category = entry.content.entity;
  var service = createService();
  service.deleteEntry(entry.id.$t, function(response) {
    privateGadgetCategories.splice(i, 1);
    showPrivateGadgetCategories();
  }, function(error) {
    category.$state = STATE_ERROR;
    category.$stateInfo = error;
    showPrivateGadgetCategories();
  });
  category.$state = STATE_PENDING;
  showPrivateGadgetCategories();
};

function addCategory() {
  var displayName = prompt('Name of new category:');
  if (displayName) {
    var service = createService();
    var entry = {xmlns: 'http://www.w3.org/2005/Atom', content: {
        type: 'application/xml', entity: {name: displayName,
        category: [{locale: 'en', displayName: displayName}]}}};
    var entity = entry.content.entity;
    service.insertEntry(privateGadgetCategoryFeedUrl, entry, function(response) {
      if (typeof(response) == 'xml') {
        entity.$state = STATE_ERROR;
        entity.$stateInfo = response;
      } else {
        entity.$state = STATE_OK;
        copyProperties(response.entry, entry);
      }
      showPrivateGadgetCategories();
    }, function(error) {
      entity.$state = STATE_OK;
      entity.$stateInfo = error;
      showPrivateGadgetCategories();
    });
    entity.$state = STATE_PENDING;
    privateGadgetCategories.push(entry); 
    showPrivateGadgetCategories();
  }
};

// ----------------
// public directory

var directoryFilterType = NO_FILTER;

function getDomainFilterListedGadgets() {
  return domainFilterListedGadgets[directoryFilterType];
};

function setDomainFilterListedGadgets(v) {
  domainFilterListedGadgets[directoryFilterType] = v;
};

function getDomainFilterListedGadgetFeedUrl() {
  return domainFilterListedGadgetFeedUrl[directoryFilterType];
};

function changeDirectoryFilter() {
  directoryFilterType = $('directory-filter-type').value;
  switch(directoryFilterType) {
    case NO_FILTER:
      disable('add-gadget');
      hide('gadget-lists');
      break;

    case WHITE_LIST_FILTER:
      show('add-gadget');
      disable('add-gadget');
      hide('hide-directory');
      show('gadget-lists');
      hide('public-gadget-list-dialog');
      $('gadget-filter-list-title').innerHTML = 'White List';
      $('gadget-filter-list').innerHTML = SPINNER;
      showGadgetFilterList();
      break;

    case BLACK_LIST_FILTER:
      show('add-gadget');
      disable('add-gadget');
      hide('hide-directory');
      show('gadget-lists');
      hide('public-gadget-list-dialog');
      $('gadget-filter-list-title').innerHTML = 'Black List';
      $('gadget-filter-list').innerHTML = SPINNER;
      showGadgetFilterList();
      break;
  }

  // TODO: set filter type on the server side
};

function getAddToFilterListButton(i, gadget) {
  return gadget.$state == STATE_PENDING ||
      isGadgetInFilterList(gadget.id, getDomainFilterListedGadgets()) ? '' :
      '<a class="right" href="javascript:void(0)" onclick="addGadgetToFilterList(' + i +
      ')">Add</a>';
};

var publicGadgetsStartIndex = 1;
var publicGadgetsMaxResults = 20;
var publicGadgetsLastSearchTerm = '';

function showPublicGadgets(loadMore) {
  hide('add-gadget');
  show('hide-directory');
  show('public-gadget-list-dialog');
  gadgets.window.adjustHeight();

  var searchTerm = trim($('search-term').value);
  if (searchTerm != publicGadgetsLastSearchTerm) {
    publicGadgets = [];
    publicGadgetsStartIndex = 1;
    publicGadgetsLastSearchTerm = searchTerm;
    $('public-gadget-list').innerHTML = '';
  }

  loadPublicGadgets(searchTerm, loadMore, function() {
    var html = [];
    for (var i = 0; i < publicGadgets.length; i++) {
      var gadget = publicGadgets[i].content.entity;
      html.push('<div class="list-item right-container" title="',
          gadget.description, '">', gadget.title,
          getAddToFilterListButton(i, gadget), '</div>');
    }
    var gadgetListElement = $('public-gadget-list');
    gadgetListElement.innerHTML = html.join('');
    if (gadgetListElement.addEventListener) {
      gadgetListElement.addEventListener('scroll', handleGadgetListScroll, false);
    } else {
      gadgetListElement.attachEvent('onscroll', handleGadgetListScroll);
    }
  });
};

function loadPublicGadgets(searchTerm, loadMore, continuation) {
  if (publicGadgets.length == 0 || loadMore) {
    var service = createService();
    service.getFeed(publicGadgetFeedUrl + '?start-index=' + publicGadgetsStartIndex +
        '&max-results=' + publicGadgetsMaxResults +
        (searchTerm ? '&q=' + searchTerm : ''), function(response) {
      if (response) {
        var more = response.feed.entry || [];
        publicGadgets = publicGadgets.concat(more);
        publicGadgetsStartIndex += more.length;
        continuation();
      }
      showElementBusy('public-gadget-list-spinner', false);
    }, showMessage);
    showElementBusy('public-gadget-list-spinner');
  } else {
    continuation();
  }
};

function handleGadgetListScroll(e) {
  var target = e.target || e.srcElement;
  if ((target.scrollTop + target.clientHeight) >= target.scrollHeight) {
    // at the bottom of the scroll bar
    showPublicGadgets(true);
  }
};

function hidePublicGadgets() {
  hide('public-gadget-list-dialog');
  hide('hide-directory');
  show('add-gadget');
};

function getGadgetUniqueName(gadgetId) {
  return 'g' + gadgetId;
};

function isGadgetInFilterList(gadgetId, filteredList) {
  for (var i = 0; i < filteredList.length; i++) {
    var gadget = filteredList[i].content.entity;
    if (gadget.gadgetId == gadgetId) {
      return true;
    }
  }

  return false;
};

function addGadgetToFilterList(i) {
  var entry = publicGadgets[i];
  var gadget = entry.content.entity;
  gadget.gadgetId = gadget.id;
  gadget.$state = STATE_PENDING;
  getDomainFilterListedGadgets().push(entry);
  showGadgetFilterList();
  showPublicGadgets();

  var newWhiteListEntry = {xmlns: 'http://www.w3.org/2005/Atom', content: {
      type: 'application/xml', entity: {name: getGadgetUniqueName(gadget.gadgetId),
      gadgetId: gadget.gadgetId}}};
  var service = createService();
  service.insertEntry(getDomainFilterListedGadgetFeedUrl(), newWhiteListEntry, function(response) {
    if (response) {
      gadget.$state = STATE_OK;
      showPublicGadgets();
    } else {
      gadget.$state = STATE_ERROR;
      gadget.$stateInfo = EMPTY_RESPONSE;
    }
    showGadgetFilterList();
  }, function(error) {
    gadget.$state = STATE_ERROR;
    gadget.$stateInfo = error;
    showGadgetFilterList();
  });
};

function removeGadgetFromFilterList(gadgetId) {
  for (var i = 0; i < getDomainFilterListedGadgets().length; i++) {
    var gadget = getDomainFilterListedGadgets()[i].content.entity;
    if (gadget.gadgetId == gadgetId) {
      var service = createService();
      service.deleteEntry(getDomainFilterListedGadgetFeedUrl() + '/g' + gadgetId, function(response) {
        getDomainFilterListedGadgets().splice(i, 1);
        gadget.$state = STATE_OK;
        showGadgetFilterList();
      }, function(error) {
        gadget.$state = STATE_ERROR;
        gadget.$stateInfo = error;
        showGadgetFilterList();
      });
      gadget.$state = STATE_PENDING;
      showGadgetFilterList();
      return;
    }
  }
};

function getFilterListItemState(gadget) {
  switch(gadget.$state) {
    case STATE_PENDING:
      return '<div class="right" style="margin:0px">' + SPINNER + '</div>';

    case STATE_ERROR:
    default:
      return '<a class="right" href="javascript:void(0)" onclick="removeGadgetFromFilterList(\'' +
          gadget.gadgetId + '\')" title="' + gadget.title + '">Remove</a>';
  }
};

function showGadgetFilterList() {
  loadGadgetFilterList(function() {
    var length = getDomainFilterListedGadgets().length;
    if (length > MAX_NO_SCROLL_ROWS) {
      var style = $('gadget-filter-list').style;
      style.height = MAX_SCROLLBAR_HEIGHT + 'px';
    }

    var html = [];
    for (var i = 0; i < length; i++) {
      var gadget = getDomainFilterListedGadgets()[i].content.entity;
      html.push('<div class="list-item right-container" title="',
          gadget.description, '">', gadget.title || 'Loading ...',
          gadget.$state == STATE_ERROR ?
              ' (<span title="' + gadget.$stateInfo + '">ERROR</span>)' : '',
          getFilterListItemState(gadget), '</div>');
    }
    $('gadget-filter-list').innerHTML = html.join('');
    gadgets.window.adjustHeight();
  });
};

function loadGadgetFilterList(continuation) {
  if (getDomainFilterListedGadgets().length == 0) {
    var service = createService();
    service.getFeed(getDomainFilterListedGadgetFeedUrl(), function(response) {
      setDomainFilterListedGadgets(response && response.feed.entry ? response.feed.entry : []);
      var length = getDomainFilterListedGadgets().length;
      var pending = length;
      for (var i = 0; i < length; i++) {
        var gadget = getDomainFilterListedGadgets()[i].content.entity;
        gadget.$state = STATE_PENDING;
        service.getEntry(publicGadgetFeedUrl + '/' + gadget.gadgetId, function(j) {
            return function(response) {
              if (response) {
                var g = response.entry.content.entity;
                g.$state = STATE_OK;
                g.gadgetId = g.id;
                getDomainFilterListedGadgets()[j] = response.entry;
              } else {
                var g = getDomainFilterListedGadgets()[j].content.entity;
                g.$state = STATE_ERROR;
                g.$stateInfo = EMPTY_RESPONSE;
              }

              if (--pending == 0) {
                enable('add-gadget');
              }

              continuation();
            }
        }(i), function(j) {
          return function(error) {
            var g = getDomainFilterListedGadgets()[j].content.entity;
            g.$state = STATE_ERROR;
            g.$stateInfo = error;

            if (--pending == 0) {
              enable('add-gadget');
            }

            continuation();
          }
        }(i));
      }
      if (pending == 0) {
        enable('add-gadget');
      }
      continuation();
    }, showMessage);
  } else {
    enable('add-gadget');
    continuation();
  }
};

// -----------------
// directory preview

var previewingDirectory = null;

function previewDirectory(directory) {
  previewingDirectory = directory;
  showDirectoryPreview();
};

function showDirectoryPreview() {
};

// -----
// other

function showMessage(message) {
  var messageBox = $('message-box');
  if (!messageBox) {
    messageBox = document.createElement('div');
    messageBox.id = 'message-box';
    document.body.insertBefore(messageBox, document.body.firstChild);
  }

  var miniMessage = new gadgets.MiniMessage(null, messageBox);
  miniMessage.createDismissibleMessage(message);
};

function detectDomainName() {
  var params = location.href.split('&');
  for (var i = 0; i < params.length; i++) {
    if (params[i].indexOf('parent=') == 0) {
      var p = params[i].split('=');
      var parent = decodeURIComponent(p[1]);
      var r = /google.com\/a\/([^\/]*)\//.exec(parent);
      var domainName = r ? r[1] : null;
      return domainName;
    }
  }

  return null;
};

function getDomainName() {
  return new gadgets.Prefs().getString("domainName") || detectDomainName();
};

function initGadget() {
  var tabset = new gadgets.TabSet(null, 'Private Directory');
  tabset.alignTabs('left', 2);
  tabset.addTab('Public Directory', {
      contentContainer: $('tab-public'),
      callback: gadgets.window.adjustHeight(),
      tooltip: 'manage domain public gadget directory'
  });
  tabset.addTab('Private Directory', {
      contentContainer: $('tab-private'),
      callback: gadgets.window.adjustHeight(),
      tooltip: 'manage domain private gadget directory'
  });
  tabset.addTab('Private Categories', {
      contentContainer: $('tab-categories'),
      callback: gadgets.window.adjustHeight(),
      tooltip: 'manage domain private gadget categories'
  });
  tabset.addTab('Directory Preview', {
      contentContainer: $('tab-preview'),
      callback: gadgets.window.adjustHeight(),
      tooltip: 'preview domain public and private directory'
  });

  var domainName = getDomainName();
  if (domainName) {
    publicGadgetFeedUrl = 'http://feedserver-enterprise.googleusercontent.com/Gadget';
    domainPublicGadgetFeedUrl = 'http://feedserver-enterprise.googleusercontent.com/a/' +
        domainName + '/Gadget';
    domainPublicGadgetFeedUrl = 'http://feedserver-enterprise.googleusercontent.com/a/' +
        domainName + '/PrivateGadget';
    domainFilterListedGadgetFeedUrl[WHITE_LIST_FILTER] =
       'http://feedserver-enterprise.googleusercontent.com/a/' + domainName +
       '/g/WhiteListedGadget';
    domainFilterListedGadgetFeedUrl[BLACK_LIST_FILTER] =
        'http://feedserver-enterprise.googleusercontent.com/a/' + domainName +
        '/g/BlackListedGadget';
    privateGadgetFeedUrl = 'http://feedserver-enterprise.googleusercontent.com/a/' +
        domainName + '/g/PrivateGadget?nocache=1';
    privateGadgetSpecFeedUrl = 'http://feedserver-enterprise.googleusercontent.com/a/' +
        domainName + '/g/PrivateGadgetSpec';
    privateGadgetCategoryFeedUrl = 'http://feedserver-enterprise.googleusercontent.com/a/' +
        domainName + '/g/PrivateGadgetCategory';
  } else {
    showMessage('Please set user preference "Domain Name"');
  }
};

function init() {
  initGadget();
  initDirectoryManager();
};

gadgets.util.registerOnLoadHandler(init);
