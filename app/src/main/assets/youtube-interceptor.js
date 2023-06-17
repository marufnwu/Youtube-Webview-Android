(function() {
    var videoItems = document.querySelectorAll('ytm-rich-item-renderer');
    console.log("#### "+videoItems.length);
    for (var i = 0; i < videoItems.length; i++) {

        var video = videoItems[i].childNodes[0].childNodes[0].childNodes[0].getAttribute('href');
        videoItems[i].addEventListener('click', function() {
           event.preventDefault();
           event.stopPropagation();
           window.YouTubeInterface.openVideo(video);
        });

    }
})();
