
public class Complex {
	
	// This variables represent the real and imaginary part of a complex number.
	private double realPart;
	private double imaginaryPart;

	// This constructor sets the real and imaginary part.
	public Complex(double real, double imag) {
		this.realPart = real;
		this.imaginaryPart = imag;
	}

	// This constructor creates a null complex number.
	public Complex() {
		this.realPart = 0;
		this.imaginaryPart = 0;
	}

	// This method returns the square of the complex number.
	public Complex square() {
		Complex c = this;
		double real = c.realPart * c.realPart - c.imaginaryPart * c.imaginaryPart;
		double imag = c.realPart * c.imaginaryPart + c.imaginaryPart * c.realPart;
		return new Complex(real, imag);
	}

	// This method multiplies the Complex number with another Complex number.
	public Complex multiply(Complex d) {
		Complex c = this;
		double re = c.realPart * c.realPart - d.imaginaryPart * d.imaginaryPart;
		double im = c.realPart * d.imaginaryPart + c.imaginaryPart * d.realPart;
		return new Complex(re, im);
	}

	// This method adds a Complex number to the complex number.
	public Complex add(Complex d) {
		return new Complex(realPart + d.getReal(), imaginaryPart + d.getImaginary());
	}

	// This method subtracts a Complex number from the complex number.
	public Complex subtract(Complex d) {
		return new Complex(realPart - d.getReal(), imaginaryPart - d.getImaginary());
	}

	// This method returns the square of the modulus of the complex number.
	public double modulusSquared() {
		return this.realPart * this.realPart + this.imaginaryPart * this.imaginaryPart;
	}

	public void setReal(double real) {
		this.realPart = real;
	}

	public void setImaginary(double imaginary) {
		this.imaginaryPart = imaginary;
	}

	public double getReal() {
		return realPart;
	}

	public double getImaginary() {
		return imaginaryPart;
	}

}
