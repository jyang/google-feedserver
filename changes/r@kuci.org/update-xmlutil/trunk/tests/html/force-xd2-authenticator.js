/**
 * @fileoverview This is a temporary approach of forcing the use of XD2
 * transport in the GData JavaScript client library.  When it has native
 * support for FeedServer, this file will become obsolete.
 */

function ForceXd2Authenticator(service) {
  google.gdata.client.Authenticator.call(this, service);
};

ForceXd2Authenticator.prototype = new google.gdata.client.Authenticator();

ForceXd2Authenticator.prototype.isAuthenticationRequired = function() {
  return true;
};

ForceXd2Authenticator.prototype.isAuthenticated = function() {
  return true;
};

ForceXd2Authenticator.prototype.setAuthHeaders = function() {
};
