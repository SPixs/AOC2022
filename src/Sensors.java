import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D.Double;
import java.awt.image.BufferedImage;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

public class Sensors {

	public static class Beacon {

			public Beacon(int x, int y) {
			 this.x = x;
			 this.y = y;
			}
			private int x;
			private int y;
			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result + x;
				result = prime * result + y;
				return result;
			}
			@Override
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				Beacon other = (Beacon) obj;
				if (x != other.x)
					return false;
				if (y != other.y)
					return false;
				return true;
			}
		
			
	}
	

	public static class Sensor {

		private int x;
		private int y;
		private int manattanDistance;

		public int getManattanDistance() {
			return manattanDistance;
		}

		public Sensor(int x, int y, int manattanDistance) {
			this.x = x;
			this.y = y;
			this.manattanDistance = manattanDistance;
		}

		public int getManattanDistance(int tx, int ty) {
			return Sensors.getManattanDistance(x, y, tx, ty);
		}

		public Shape getShape() {
			Path2D shape = new Path2D.Double();
			shape.moveTo(x-manattanDistance, y);
			shape.lineTo(x, y-manattanDistance);
			shape.lineTo(x+manattanDistance, y);
			shape.lineTo(x, y+manattanDistance);
			shape.closePath();
			return shape;
		}
	}

	public static void main(String[] args) throws IOException {
			Stream<String> lines = Files.lines(Paths.get("./input_sensors.txt"));
			List<String> collect = lines.collect(Collectors.toList());
			
//			Sensor at x=2, y=18: closest beacon is at x=-2, y=15

			int minX = Integer.MAX_VALUE;
			int maxX = Integer.MIN_VALUE;
			
			List<Sensor> sensors = new ArrayList<Sensors.Sensor>();
			Set<Beacon> beacons = new HashSet<Sensors.Beacon>();
			for (String s : collect) {
				String[] split = s.split(":");
				String coordinates1Text = split[0].substring(split[0].indexOf('x'));
				String coordinates2Text = split[1].substring(split[1].indexOf('x'));
				
				int x1 = Integer.parseInt(coordinates1Text.split(",")[0].trim().split("=")[1]);
				int y1 = Integer.parseInt(coordinates1Text.split(",")[1].trim().split("=")[1]);
				int x2 = Integer.parseInt(coordinates2Text.split(",")[0].trim().split("=")[1]);
				int y2 = Integer.parseInt(coordinates2Text.split(",")[1].trim().split("=")[1]);
				
				Sensor sensor = new Sensor(x1, y1, getManattanDistance(x1,y1,x2,y2));
				sensors.add(sensor);
				beacons.add(new Beacon(x2, y2));
				
				System.out.println(x1 + " " + y1 + " " + sensor.getManattanDistance());
				
				minX = Math.min(minX, x1 - sensor.getManattanDistance());
				maxX = Math.max(maxX, x1 + sensor.getManattanDistance());
			}			
			
			int result = 0;
			System.out.println(minX + " " + maxX);
//			int y=10;
			int y=2000000;
			for (int x = minX;x<=maxX;x++) {
				boolean cannotContainBeacon = false;
				Iterator<Sensor> sensorsIterator = sensors.iterator();
				while (!cannotContainBeacon && sensorsIterator.hasNext()) {
					Sensor sensor = sensorsIterator.next();
					int distance = sensor.getManattanDistance(x, y);
					cannotContainBeacon |= distance <= sensor.getManattanDistance();
					cannotContainBeacon &= !beacons.contains(new Beacon(x, y));
				}
				if (cannotContainBeacon) result++;
			}
			
			System.out.println(result);

			int maxSize = 20;
//			int maxSize = 4000000;
			Shape shape = new Rectangle2D.Double(0, 0, maxSize+1, maxSize+1);
			Area a = new Area(shape);
			int index = 0;
			for (Sensor sensor : sensors) {
				
				a.subtract(new Area(sensor.getShape()));
				BufferedImage image = new BufferedImage(maxSize, maxSize, BufferedImage.TYPE_INT_RGB);
				Graphics2D g2d= (Graphics2D) image.getGraphics();
				g2d.setColor(Color.RED);
				g2d.fill(a);
//				g2d.draw(sensor.getShape());
				ImageIO.write(image, "PNG", new File("image"+(++index)+".png"));
			}
			System.out.println(a.getBounds());

			BufferedImage image = new BufferedImage(maxSize, maxSize, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d= (Graphics2D) image.getGraphics();
			g2d.setColor(Color.RED);
			g2d.draw(new Sensor(2, 2, 2).getShape());
			ImageIO.write(image, "PNG", new File("image.png"));
			
			
//			Sensor sensor = new Sensor(8, 7, getManattanDistance(8, 7, 2, 10));
//			for (int y=-2;y<=22;y++) {
//				for (int x = -2;x<=25;x++) {
//					boolean cannotContainBeacon = sensor.getManattanDistance(x, y) <= sensor.getManattanDistance();
//					char c = cannotContainBeacon ? '#' : '.';
//					if (x==8 && y==7) c = 'S';
//					if (x==2 && y==10) c = 'B';
//					System.out.print(c);
//				}
//				System.out.println();
//			}
			
//			System.out.println(sensor.getManattanDistance());
//			System.out.println(sensor.getManattanDistance(7,7));
	}

	private static int getManattanDistance(int x1, int y1, int x2, int y2) {
		return Math.abs(x2-x1)+Math.abs(y2-y1);
	}

}

