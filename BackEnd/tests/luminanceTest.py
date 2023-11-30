#If want to test this function, please put it in a python project
from PIL import Image

def calculate_average_luminance(image_path):
    # Open the image file
    with Image.open(image_path) as img:
        # Convert the image to grayscale
        grayscale = img.convert('L')

        # Calculate the average luminance
        total_luminance = 0
        for pixel in list(grayscale.getdata()):
            total_luminance += pixel
        average_luminance = total_luminance / (grayscale.width * grayscale.height)

        # Normalize the luminance to a 0 to 1 scale
        normalized_luminance = average_luminance / 255

        return normalized_luminance

# Example Usage, adjust the image path as required
image_path = './phoneScreen.png'
average_luminance = calculate_average_luminance(image_path)
print("The average luminance of the image on a scale from 0 to 1 is: {average_luminance}")


