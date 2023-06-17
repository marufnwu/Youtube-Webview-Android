(function() {
    var thumbnails = document.querySelectorAll('a#thumbnail');

    for (var i = 0; i < thumbnails.length; i++) {
        var thumbnail = thumbnails[i];
        thumbnail.addEventListener('click', function(event) {
            event.preventDefault();
            var videoId = this.getAttribute('href').split('=')[1];
            window.YouTubeInterface.onThumbnailClicked(videoId);
        });
    }
})();