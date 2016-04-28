function popup(challengeName) {
	var value = challengeName.value;
	var edited = "<pre>" + value.split("\n").join("<br>") + "</pre>";
    w2popup.open({
        title: 'Solution',
        body: '<div class="w2ui">' + edited + '</div>'
    });
}