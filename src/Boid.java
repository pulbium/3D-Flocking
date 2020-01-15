import java.awt.Color;
import java.util.Random;

import javax.swing.JPanel;

public class Boid {

	int radius = 8, perceptionRadius = 50;
	float maxSpeed = 4, maxForce = 1; 
	float[] position = new float[3], velocity = new float[3], acceleration = new float[3];
	Color color;
	
	public Boid(int width, int height, float maxSpeed, float maxForce) {
		Random rand = new Random();
		position[0] = rand.nextFloat()*(float)width;
		position[1] = rand.nextFloat()*(float)height;
		position[2] = rand.nextFloat()*(float)height;
		
		
		velocity[0] = rand.nextFloat() - (float)1/2;
		velocity[1] = rand.nextFloat() - (float)1/2;
		velocity[2] = rand.nextFloat() - (float)1/2;
		
		normalize(velocity, rand.nextFloat()*maxSpeed);
		acceleration[0] = 0;
		acceleration[1] = 0;
		acceleration[2] = 0;
		this.maxSpeed = maxSpeed;
		this.maxForce = maxForce;
		int r,g,b;
		do {
			r = rand.nextInt(255);
			g = rand.nextInt(255);
			b = rand.nextInt(255);
		}while(r + g + b>550);
		color = new Color(r, g, b);
	}
	
	float distanceTo(Boid other, JPanel g) {
		return distanceTo(other.position[0], other.position[1], other.position[2], g);
	}
	
	float distanceTo(float x, float y, float z, JPanel g) { //seems to be OK
		float realDiffX, realDiffY, realDiffZ;
		
		float xDiff1 = Math.abs(position[0] - x);
		float xDiff2 = Math.abs(position[0] - (x - g.getWidth()));
		
		if (xDiff1 < xDiff2) {
			realDiffX = xDiff1;
		} else {realDiffX = xDiff2;}
		
		float yDiff1 = Math.abs(position[1] - y);
		float yDiff2 = Math.abs(position[1] - (y - g.getHeight()));
		
		if (yDiff1 < yDiff2) {
			 realDiffY = yDiff1;
		} else {realDiffY = yDiff2;}
		
		float zDiff1 = Math.abs(position[2] - z);
		float zDiff2 = Math.abs(position[2] - (z - g.getHeight()));
		
		if (zDiff1 < zDiff2) {
			 realDiffZ = zDiff1;
		} else {realDiffZ = zDiff2;}
		
		return (float)Math.sqrt((realDiffX * realDiffX) + (realDiffY * realDiffY) + (realDiffZ * realDiffZ));
	}
	
	float[] normalize(float[] v, float n) {
		for(int i = 0; i < 3; i++) 
			v[i] *= n/length(v);
		return v;
	}
	
	float length(float[] v) {
		return (float)Math.sqrt(v[0]*v[0] + v[1]*v[1] + v[2]*v[2]);
	}
	
	boolean isInTheRadius(Boid other, JPanel g) { 
		if(!other.equals(this) && (distanceTo(other, g) < perceptionRadius))
			return true;
		else
			return false;
	}
	
	
	float[] alignment(Boid[] boids, JPanel g) {
		float[] steering = {0, 0, 0};
		int count = 0;
		for(Boid boid: boids) {
			if(isInTheRadius(boid, g)) {
				steering[0] += boid.velocity[0];
				steering[1] += boid.velocity[1];
				steering[2] += boid.velocity[2];
				count++;
			}
		}
		if(count > 0) {
			steering[0]/=count;
			steering[1]/=count;
			steering[2]/=count;
			
			normalize(steering,maxSpeed);
			
			steering[0] -= velocity[0];
			steering[1] -= velocity[1];
			steering[2] -= velocity[2];
			
			//if(length(steering) > maxForce)
				normalize(steering, maxForce/8);
		}
		return steering;
	}
	
	float[] separation(Boid[] boids, JPanel g) {
		float[] steering = {0, 0, 0};
		int count = 0;
		for(Boid boid: boids) {
			float[] diff = new float[3];
			if(isInTheRadius(boid, g)) {
				if(Math.abs(position[0] - boid.position[0]) > perceptionRadius) {
					if(position[0] < boid.position[0])
						diff[0] = position[0] - (boid.position[0] - g.getWidth());
					else
						diff[0] = (position[0] - g.getWidth()) - boid.position[0];
				}else
					diff[0] = position[0] - boid.position[0];
				
				if(Math.abs(position[1] - boid.position[1]) > perceptionRadius) {
					if(position[1] < boid.position[1])
						diff[1] = position[1] - (boid.position[1] - g.getHeight());
					else
						diff[1] = (position[1] - g.getHeight()) - boid.position[1];
				}else
					diff[1] = position[1] - boid.position[1];
				
				if(Math.abs(position[2] - boid.position[2]) > perceptionRadius) {
					if(position[2] < boid.position[2])
						diff[2] = position[2] - (boid.position[2] - g.getHeight());
					else
						diff[2] = (position[2] - g.getHeight()) - boid.position[2];
				}else
					diff[2] = position[2] - boid.position[2];
				normalize(diff, 1);
				
				steering[0] += diff[0]/distanceTo(boid, g);
				steering[1] += diff[1]/distanceTo(boid, g);
				steering[2] += diff[2]/distanceTo(boid, g);
				count++;
			}
		}
		if(count > 0) {
			steering[0]/=count;
			steering[1]/=count;
			steering[2]/=count;

			normalize(steering, maxSpeed);
			
			steering[0] -= velocity[0];
			steering[1] -= velocity[1];
			steering[2] -= velocity[2];
			
			if(length(steering) > maxForce)
				normalize(steering, maxForce);
		}
		return steering;
	}
	
	
	float[] cohesion(Boid[] boids, JPanel g) {
		float[] steering = {0, 0, 0};
		int count = 0;
		for(Boid boid: boids) {
			if(isInTheRadius(boid, g)) {
				if(Math.abs(position[0] - boid.position[0]) > perceptionRadius) {
					if(position[0] < boid.position[0])
						steering[0] += boid.position[0] + g.getWidth();
					else
						steering[0] += boid.position[0] - g.getWidth();
				}else
					steering[0] += boid.position[0];
				
				if(Math.abs(position[1] - boid.position[1]) > perceptionRadius) {
					if(position[1] < boid.position[1])
						steering[1] += boid.position[1] + g.getHeight();
					else
						steering[1] += boid.position[1] - g.getHeight();
				}else
					steering[1] += boid.position[1];
				
				if(Math.abs(position[2] - boid.position[2]) > perceptionRadius) {
					if(position[2] < boid.position[2])
						steering[2] += boid.position[2] + g.getHeight();
					else
						steering[2] += boid.position[2] - g.getHeight();
				}else
					steering[2] += boid.position[2];
				
				count++;
			}
		}
		if(count > 0) {
			steering[0]/=count;
			steering[1]/=count;
			steering[2]/=count;
			steering[0]-=position[0];
			steering[1]-=position[1];
			steering[2]-=position[2];
			
			normalize(steering,maxSpeed);
			
			steering[0] -= velocity[0];
			steering[1] -= velocity[1];
			steering[1] -= velocity[2];
			
			//if(length(steering) > maxForce)
				normalize(steering, maxForce/10);
		}
		return steering;
	}
	
	void flock(Boid[] boids, JPanel g, float cohesionMultiplier, float separationMultiplier, float alignmentMultiplier) {
		
		float[] alignment = alignment(boids, g);
		float[] cohesion = cohesion(boids, g);
		float[] separation = separation(boids, g);
		
		for(int i = 0; i < 3; i++) {
		acceleration[i] = alignment[i]*alignmentMultiplier/10
				+ cohesion[i]*cohesionMultiplier/10
				+ separation[i]*separationMultiplier/10;
		}
	}
	
	void update(JPanel g) {
		position[0] += velocity[0];
		position[1] += velocity[1];
		position[2] += velocity[2];
		
		if(length(velocity) > maxSpeed)
			normalize(velocity, maxSpeed);
		
		velocity[0] += acceleration[0];
		velocity[1] += acceleration[1];
		velocity[2] += acceleration[2];
		
		edges(g);
	}
	
	void edges(JPanel g) {
		if(position[0] > g.getWidth())
			position[0] = 0;
		if(position[0] < 0)
			position[0] = g.getWidth();
		if(position[1] > g.getHeight())
			position[1] = 0;
		if(position[1] < 0)
			position[1] = g.getHeight();
		if(position[2] > g.getHeight())
			position[2] = 0;
		if(position[2] < 0)
			position[2] = g.getHeight();
	}
}