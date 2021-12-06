
// version of document.  explainer starts at 1 and bumps
// forward for ever detected change
var version = null;

var POLL_TIME = 333; // Milliseconds

// checks to see if the document or its dependents have
// been updated (aka if version has been bumped.)
function seeIfPageUpdated(){
    fetch('/version')
	.then(response => response.json())
	.then(data => {
	    // first time version is null.
	    if (version==null){
		version = data;
		setTimeout( seeIfPageUpdated, POLL_TIME);
	    } else if (version != data){
		// reload is nice because the browser
		// tends to preserve the location in the document
		window.location.reload()
	    } else {
		setTimeout( seeIfPageUpdated, POLL_TIME);
	    }
	});
}

// kick off polling
setTimeout( seeIfPageUpdated, POLL_TIME);





