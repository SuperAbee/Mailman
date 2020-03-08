import requests 

def compose(fps, num_of_pictures, workplace, image_prefix, delete_images):
    url = ('http://localhost:9123/video/compose?' 
    + 'fps=' + str(fps) 
    + '&numOfPictures=' + str(num_of_pictures)
    + '&workplace=' + workplace
    + '&imagePrefix=' + image_prefix
    + '&deleteImages=' + str(delete_images))

    request = requests.get(url)

    print (request.content)

def decompose(workplace, image_prefix, video_name):
    url = ('http://localhost:9123/video/decompose?' 
    + 'workplace=' + workplace
    + '&imagePrefix=' + image_prefix
    + '&videoName=' + video_name)

    request = requests.get(url)

    print (request.content)


if __name__ == '__main__':
    #decompose('D:/Mailman/Workplace', 'decode', 'in.mp4')
    #compose(5, 19, 'D:/Mailman/Workplace', 'tmp', False)