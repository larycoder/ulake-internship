import math

from PIL import Image, ImageChops
import cv2
from keras_retinanet.utils.colors import label_color
import numpy as np


def draw_mask(image, box, mask, label=None, color=None, binarize_threshold=0.5):
    """ Draws a mask in a given box.

    Args
            image              : Three dimensional image to draw on.
            box                : Vector of at least 4 values (x1, y1, x2, y2) representing a box in the image.
            mask               : A 2D float mask which will be reshaped to the size of the box, binarized and drawn over the image.
            color              : Color to draw the mask with. If the box has 5 values, the last value is assumed to be the label and used to construct a default color.
            binarize_threshold : Threshold used for binarizing the mask.
    """
    if label is not None:
        color = label_color(label)
    if color is None:
        color = (255, 0, 0)

    # resize to fit the box
    mask = mask.astype(np.float32)
    mask = cv2.resize(mask, (box[2] - box[0], box[3] - box[1]))

    # binarize the mask
    mask = (mask > binarize_threshold).astype(np.uint8)

    # draw the mask in the image
    mask_image = np.zeros((image.shape[0], image.shape[1]), np.uint8)
    mask_image[box[1]:box[3], box[0]:box[2]] = mask
    mask = mask_image

    # compute a nice border around the mask
    border = mask - cv2.erode(mask, np.ones((5, 5), np.uint8), iterations=1)

    # apply color to the mask and border
    border = (np.stack([border] * 3, axis=2) *
              (255, 255, 255)).astype(np.uint8)

    # draw the border
    indices = np.where(border != [0, 0, 0])
    image[indices[0], indices[1], :] = 0.2 * image[indices[0],
                                                   indices[1], :] + 0.8 * border[indices[0], indices[1], :]


def calculateNoduleSize(x1, y1, x2, y2):
    dist = math.sqrt((float(x2) - float(x1)) ** 2 +
                     (float(y2) - float(y1)) ** 2)
    return dist


def nms(output, nms_th):
    if len(output) == 0:
        return output

    output = np.array(output, dtype=object)
    output = output[np.argsort(-output[:, 0])]

    bboxes = [output[0]]
    print("Lenghth ", len(output))

    for i in np.arange(1, len(output)):
        bbox = output[i]
        bbox2 = np.array([output[i][1], output[i][2][0],
                         output[i][2][1], output[i][3]])
        flag = 1
        for j in range(len(bboxes)):
            bboxes2 = np.array(
                [bboxes[j][1], bboxes[j][2][0], bboxes[j][2][1], bboxes[j][3]])
            if iou(bbox2, bboxes2) >= nms_th:
                flag = -1
                break
        if flag == 1:
            bboxes.append(bbox)

    bboxes = np.asarray(bboxes, dtype=object)
    return bboxes


def iou(box0, box1):

    r0 = box0[3] / 2
    s0 = box0[:3] - r0
    e0 = box0[:3] + r0

    r1 = box1[3] / 2
    s1 = box1[:3] - r1
    e1 = box1[:3] + r1

    overlap = []
    for i in range(len(s0)):
        overlap.append(max(0, min(e0[i], e1[i]) - max(s0[i], s1[i])))

    intersection = overlap[0] * overlap[1] * overlap[2]
    union = box0[3] * box0[3] * box0[3] + \
        box1[3] * box1[3] * box1[3] - intersection
    return intersection / union


def trim(im):
    bg = Image.new(im.mode, im.size, im.getpixel((0, 0)))
    diff = ImageChops.difference(im, bg)
    diff = ImageChops.add(diff, diff, 1.0, -50)
    bbox = diff.getbbox()
    if bbox:
        return im.crop(bbox)


def compute_resize_scale(image_shape, min_side=512, max_side=512):
    """ Compute an image scale such that the image size is constrained to min_side and max_side.
    Args
            min_side: The image's min side will be equal to min_side after resizing.
            max_side: If after resizing the image's max side is above max_side, resize until the max side is equal to max_side.
    Returns
            A resizing scale.
    """
    (rows, cols, _) = image_shape

    smallest_side = min(rows, cols)

    # rescale the image so the smallest side is min_side
    scale = min_side / smallest_side

    # check if the largest side is now greater than max_side, which can happen
    # when images have a large aspect ratio
    largest_side = max(rows, cols)
    if largest_side * scale > max_side:
        scale = max_side / largest_side

    return scale


def resize_image(img, min_side=512, max_side=512):
    # compute scale to resize the image
    scale = compute_resize_scale(
        img.shape, min_side=min_side, max_side=max_side)

    # resize the image with the computed scale
    img = cv2.resize(img, None, fx=scale, fy=scale)

    return img, scale
