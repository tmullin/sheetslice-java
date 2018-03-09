jQuery(function($) {
    var $entries = $('#entries'),
        $formTarget = $('#upload-form-target');

    function clearEntries() {
        $entries.empty();
    }

    function addEntry(data) {
        $('<tr/>')
            .appendTo($entries)
            .append($('<td/>').append(data));
    }

    function createEntryLink(prefix, text) {
        var $link = $('<a href="javascript:;"/>').text(text || prefix.replace(/^cache\//, '') || '(blank)');

        if (prefix.match(/\/$/)) {
            $link.click(function() {
                loadEntries(prefix);
                return false;
            });
        } else {
            $link
                .attr('href', 'http://sheetslice-s3.tmullin.net/' + prefix)
                .attr('target', '_blank');
        }

        return $link;
    }

    function loadEntries(prefix) {
        $.get('/pdfs/' + prefix.replace(/^cache\//, '')).done(function(result) {
            clearEntries();

            addEntry(createEntryLink(prefix.replace(/[^/]+\/$/, ''), '../'));
            addEntry(createEntryLink(prefix));

            $.each(result, function(i, item) {
                addEntry(createEntryLink(item));
            });
        });
    }

    $formTarget.load(function() {
        clearEntries();

        var contents = $formTarget.contents().text();

        if (!contents) {
            return;
        }

        var data = JSON.parse(contents);
        loadEntries(data.result);
    });
});
