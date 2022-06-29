package com.globits.da.utils;

import java.io.*;
import java.util.*;

import com.globits.da.dto.EmployeeDto;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletResponse;

public class ImportExportExcelUtil {
	private static List<String> columnConfig = Arrays.asList(
		"code", "name", "email", "phone", "age", "provinceId", "districtId", "communeId"
	);

	private static void createEmployeeSheetHeader(Sheet sheet, int rowIndex) {
		Row row = sheet.createRow(rowIndex);
		Cell cell;

		cell = row.createCell(0);
		cell.setCellValue("No");

		int index = 1;

		for (String columnName: columnConfig) {
			cell = row.createCell(index, CellType.STRING);
			cell.setCellValue(columnName);
			index++;
		}
	}
	
	public static File exportEmployeeList(List<EmployeeDto> list) throws IOException {
		try (
			Workbook workbook = new XSSFWorkbook();
		) {
			Sheet sheet = workbook.createSheet("Employees");
			Row row;
			Cell cell;
			createEmployeeSheetHeader(sheet, 0);
			int numberRow = list.size();
			int numberCell = columnConfig.size();
			for (int i = 0; i < numberRow; i++) {
				row = sheet.createRow(i + 1);

				cell = row.createCell(0);
				cell.setCellValue(i + 1);

				EmployeeDto dto = list.get(i);

				for (int j = 1; j <= numberCell; j++) {
					cell = row.createCell(j);
					switch (j - 1) {
						case 0:
							cell.setCellValue(dto.getCode());
							break;
						case 1:
							cell.setCellValue(dto.getName());
							break;
						case 2:
							cell.setCellValue(dto.getEmail());
							break;
						case 3:
							cell.setCellValue(dto.getPhone());
							break;
						case 4:
							cell.setCellValue(dto.getAge());
							break;
						case 5:
							cell.setCellValue(dto.getProvinceId().toString());
							break;
						case 6:
							cell.setCellValue(dto.getDistrictId().toString());
							break;
						case 7:
							cell.setCellValue(dto.getCommuneId().toString());
							break;
						default:
							break;
					}
				}
			}
			File file = new File("Employees.xlsx");
			try (FileOutputStream outputStream = new FileOutputStream(file);) {
				workbook.write(outputStream);
			}
			return file;
		}
	}

	public static Workbook getWorkbook(InputStream inputStream, String fileName)
	throws IOException {
		if (fileName.endsWith("xlsx")) {
			return new XSSFWorkbook(inputStream);
		} else {
			throw new IllegalArgumentException("File format is incorrect!");
		}
	}

	public static Object getCellValue(Cell cell) {
		CellType cellType = cell.getCellTypeEnum();
		Object cellValue = null;
		switch (cellType) {
			case STRING:
				cellValue = cell.getStringCellValue();
				break;
			case NUMERIC:
				cellValue = cell.getNumericCellValue();
				break;
			case BOOLEAN:
				cellValue = cell.getBooleanCellValue();
				break;
			case FORMULA:
				cellValue = cell.getCellFormula();
				break;
			default:
				break;
		}
		return cellValue;
	}
	
	public static List<EmployeeDto> importEmployeeList(InputStream inputStream, String fileName)
	throws IOException {
		List<EmployeeDto> employeeList = new ArrayList<>();

		Workbook workbook = getWorkbook(inputStream, fileName);
		Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.rowIterator();

		if(rowIterator.hasNext()) {
			rowIterator.next();
		}

		while (rowIterator.hasNext()) {
			Row nextRow = rowIterator.next();
			Iterator<Cell> cellIterator = nextRow.cellIterator();

			EmployeeDto employee = new EmployeeDto();

			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				Object cellValue = getCellValue(cell);
				if (cellValue == null || cellValue.toString().isEmpty()) {
					continue;
				}
				int columnIndex = cell.getColumnIndex();
				switch (columnIndex) {
					case 1:
						employee.setCode(cellValue.toString());
						break;
					case 2:
						employee.setName(cellValue.toString());
						break;
					case 3:
						employee.setEmail(cellValue.toString());
						break;
					case 4:
						employee.setPhone(cellValue.toString());
						break;
					case 5:
						employee.setAge(((Double) cellValue).intValue());
						break;
					case 6:
						employee.setProvinceId(UUID.fromString(cellValue.toString()));
						break;
					case 7:
						employee.setDistrictId(UUID.fromString(cellValue.toString()));
						break;
					case 8:
						employee.setCommuneId(UUID.fromString(cellValue.toString()));
						break;
					default:
						break;
				}
			}

			employeeList.add(employee);
		}

		return employeeList;
	}
}
