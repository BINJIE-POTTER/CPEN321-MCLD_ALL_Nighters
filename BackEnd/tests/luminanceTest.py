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

# Example Usage
image1_path = './phoneScreen.png'
image2_path = './phoneButton.png'
average_luminance1 = calculate_average_luminance(image1_path)
average_luminance2 = calculate_average_luminance(image2_path)

print(f"The average luminance of the image 1 on a scale from 0 to 1 is: {average_luminance1}")
print(f"The average luminance of the image 2 on a scale from 0 to 1 is: {average_luminance2}")